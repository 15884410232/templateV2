package com.levy.collection.service.store;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;
import org.springframework.util.unit.DataSize;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 本地磁盘存储
 *
 * @author Wangwang Tang
 * @since 2024-07-31
 */
@Slf4j
public class LocalStorage implements Storage {

    private static final String FILE_LCK_SUFFIX = ".lck";
    private final String path;

    public LocalStorage(String path) {
        Assert.notNull(path, "path must not be null");
        this.path = path;
    }

    @Override
    public String separator() {
        return File.separator;
    }

    @Override
    public boolean existBucket(String bucketName) {
        return Files.exists(Paths.get(path, bucketName).normalize());
    }

    @Override
    public void makeBucket(String bucketName) {
        Path bucket = Paths.get(path, bucketName).normalize();
        if (!Files.exists(bucket) && !bucket.toFile().mkdirs()) {
            throw new RuntimeException("Create bucket failed");
        }
    }

    @Override
    public void putObject(String bucketName, String objectName) {
        Path object = Paths.get(path, bucketName, objectName).normalize();
        if (Files.exists(object) && !Files.isDirectory(object)) {
            throw new RuntimeException();
        }
        if (!Files.exists(object) && !object.toFile().mkdirs()) {
            throw new RuntimeException();
        }
    }

    @Override
    public void putObject(String bucketName, String objectName, Path objectPath) {
        Path targetPath = Paths.get(path, bucketName, objectName).normalize();
        Path targetLckPath = Paths.get(path, bucketName, objectName + FILE_LCK_SUFFIX).normalize();
        try {
            try (InputStream is = Files.newInputStream(objectPath, StandardOpenOption.READ);
                 OutputStream os = Files.newOutputStream(targetLckPath, StandardOpenOption.CREATE, StandardOpenOption.WRITE)) {
                IOUtils.copyLarge(is, os);
            }
            if (!targetLckPath.toFile().renameTo(targetPath.toFile())) {
                throw new RuntimeException();
            }
        } catch (IOException e) {
            throw new RuntimeException();
        } finally {
            if (Files.exists(targetLckPath)) {
                try {
                    Files.delete(targetLckPath);
                } catch (IOException ignored) {
                    log.error("Delete target Lck path failed. ");
                }
            }
        }
    }

    @Override
    public void putObject(String bucketName, String objectName, InputStream inputStream) {
        Path targetPath = Paths.get(path, bucketName, objectName).normalize();
        Path targetLckPath = Paths.get(path, bucketName, objectName + FILE_LCK_SUFFIX).normalize();
        try {
            try (OutputStream os = Files.newOutputStream(targetLckPath, StandardOpenOption.CREATE, StandardOpenOption.WRITE)) {
                IOUtils.copyLarge(inputStream, os);
            }
            if (!targetLckPath.toFile().renameTo(targetPath.toFile())) {
                throw new RuntimeException();
            }
        } catch (IOException e) {
            throw new RuntimeException();
        } finally {
            if (Files.exists(targetLckPath)) {
                try {
                    Files.delete(targetLckPath);
                } catch (IOException ignored) {
                }
            }
        }
    }

    @Override
    public void composeObject(String bucketName, List<String> partObjectNames, String composeObjectName) {
        Path completePath = Paths.get(path, bucketName, composeObjectName);
        Path completeLckPath = Paths.get(path, bucketName, composeObjectName + FILE_LCK_SUFFIX);
        if (Files.exists(completePath)) {
            return;
        }
        try {
            try (OutputStream os = Files.newOutputStream(completeLckPath, StandardOpenOption.CREATE, StandardOpenOption.WRITE)) {
                for (String partObjectName : partObjectNames) {
                    Path partPath = Paths.get(path, bucketName, partObjectName);
                    try (InputStream is = Files.newInputStream(partPath, StandardOpenOption.READ)) {
                        IOUtils.copyLarge(is, os);
                    }
                }
            }
            if (!completeLckPath.toFile().renameTo(completePath.toFile())) {
                throw new RuntimeException();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (Files.exists(completeLckPath)) {
                try {
                    Files.delete(completeLckPath);
                } catch (IOException ignored) {
                }
            }
        }
    }

    @Override
    public DataSize getObjectSize(String bucketName, String objectName) {
        try {
            Path object = Paths.get(path, bucketName, objectName);
            return Files.exists(object) ? DataSize.ofBytes(Files.size(object)) : null;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean existObject(String bucketName, String objectName) {
        Path object = Paths.get(path, bucketName, objectName);
        return Files.exists(object);
    }

    @Override
    public void removeObject(String bucketName, String objectName, boolean recursive) {
        Path object = Paths.get(path, bucketName, objectName);
        if (!Files.exists(object)) {
            return;
        }
        try {
            if (recursive) {
                Files.walkFileTree(object, new SimpleFileVisitor<Path>() {
                    @Override
                    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                        Files.delete(file);
                        return FileVisitResult.CONTINUE;
                    }

                    @Override
                    public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                        if (exc == null) {
                            Files.delete(dir);
                            return FileVisitResult.CONTINUE;
                        } else {
                            throw exc;
                        }
                    }
                });
            } else {
                Files.delete(object);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // TODO 需要加入 recursive start limit 的校验
    @Override
    public List<String> listObjects(String bucketName, String prefix, boolean recursive, String start, Integer limit) {
        File baseDir = Paths.get(path, bucketName).normalize().toFile();
        String baseDirPath = baseDir.getAbsolutePath();
        File object = Paths.get(path, bucketName, prefix).normalize().toFile();
        if (object.exists() && object.isDirectory()) {
            return Stream.of(Optional.ofNullable(object.list()).orElse(new String[0]))
                    .sorted()
                    .map(c -> new File(object, c))
                    .map(File::getAbsolutePath)
                    .map(o -> StringUtils.removeStart(o, baseDirPath))
                    .map(o -> StringUtils.removeStart(o, separator()))
                    .collect(Collectors.toList());
        } else {
            File parent = object.getParentFile();
            String prefixName = object.getName();
            return Stream.of(Optional.ofNullable(parent.list()).orElse(new String[0]))
                    .filter(s -> s.startsWith(prefixName))
                    .sorted()
                    .map(c -> new File(object, c))
                    .map(File::getAbsolutePath)
                    .map(s -> StringUtils.removeStart(s, baseDirPath))
                    .map(o -> StringUtils.removeStart(o, separator()))
                    .collect(Collectors.toList());
        }
    }

    @Override
    public InputStream getObject(String bucketName, String objectName) {
        try {
            return Files.newInputStream(Paths.get(path, bucketName, objectName).normalize(), StandardOpenOption.READ);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
