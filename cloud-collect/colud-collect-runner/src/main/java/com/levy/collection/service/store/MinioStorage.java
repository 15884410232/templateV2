package com.levy.collection.service.store;

import io.minio.*;
import io.minio.errors.ErrorResponseException;
import io.minio.errors.MinioException;
import io.minio.messages.DeleteError;
import io.minio.messages.DeleteObject;
import io.minio.messages.Item;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;
import org.springframework.util.unit.DataSize;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Minio存储
 *
 * @author Wangwang Tang
 * @since 2024-08-01
 */
@Slf4j
public class MinioStorage implements Storage {

    private static final String SEPARATOR = "/";
    private final MinioClient client;

    public MinioStorage(MinioClient minioClient) {
        Assert.notNull(minioClient, "minioClient must not be null");
        this.client = minioClient;
    }

    @Override
    public String separator() {
        return SEPARATOR;
    }

    @Override
    public boolean existBucket(String bucketName) {
        try {
            return client.bucketExists(BucketExistsArgs.builder()
                    .bucket(bucketName)
                    .build());
        } catch (MinioException e) {
            throw new RuntimeException(e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void makeBucket(String bucketName) {
        if (!existBucket(bucketName)) {
            try {
                client.makeBucket(MakeBucketArgs.builder()
                        .bucket(bucketName)
                        .build());
            } catch (MinioException e) {
                throw new RuntimeException(e.getMessage(), e);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void putObject(String bucketName, String objectName) {
        try {
            String object = objectName.endsWith(separator()) ? objectName : objectName + separator();
            client.putObject(PutObjectArgs.builder()
                    .bucket(bucketName)
                    .object(object)
                    .stream(new ByteArrayInputStream(new byte[0]), 0, -1)
                    .build());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void putObject(String bucketName, String objectName, InputStream inputStream) {
        try {
            client.putObject(PutObjectArgs.builder()
                    .bucket(bucketName)
                    .object(objectName)
                    .stream(inputStream, inputStream.available(), -1)
                    .build());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void composeObject(String bucketName, List<String> partObjectNames, String composeObjectName) {
        List<ComposeSource> sourceObjectList = partObjectNames.stream()
                .map(partObjectName -> ComposeSource.builder().bucket(bucketName).object(partObjectName).build())
                .collect(Collectors.toList());
        try {
            client.composeObject(ComposeObjectArgs.builder()
                    .bucket(bucketName)
                    .object(composeObjectName)
                    .sources(sourceObjectList)
                    .build());
        } catch (MinioException e) {
            throw new RuntimeException(e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public DataSize getObjectSize(String bucketName, String objectName) {
        // 获取对象的元数据
        try {
            StatObjectResponse stat = client.statObject(StatObjectArgs.builder()
                    .bucket(bucketName)
                    .object(objectName)
                    .build());
            return DataSize.ofBytes(stat.size());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean existObject(String bucketName, String objectName) {
        try {
            client.statObject(StatObjectArgs.builder()
                    .bucket(bucketName)
                    .object(objectName)
                    .build());
            return true; // 对象存在
        } catch (ErrorResponseException e) {
            if (e.errorResponse().code().equals("NoSuchKey")) {
                return false; // 对象不存在
            }
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void removeObject(String bucketName, String objectName) {
        try {
            client.removeObject(RemoveObjectArgs.builder()
                    .bucket(bucketName)
                    .object(objectName)
                    .build());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void removeObject(String bucketName, String objectName, boolean recursive) {
        if (recursive) {
            Set<String> removedObjectNames = new HashSet<>();
            List<String> objectNames = listObjects(bucketName, objectName, true);
            while (!objectNames.isEmpty()) {
                removedObjectNames.addAll(objectNames);
                removeObjects(bucketName, objectNames);
                objectNames = listObjects(bucketName, objectName, true);
                objectNames.removeAll(removedObjectNames);
            }
        }
        removeObject(bucketName, objectName);
    }

    @Override
    public void removeObjects(String bucketName, Collection<String> objectNames) {
        try {
            List<DeleteObject> objects = objectNames.stream()
                    .map(DeleteObject::new)
                    .collect(Collectors.toList());
            Iterable<Result<DeleteError>> errors = client.removeObjects(RemoveObjectsArgs.builder()
                    .bucket(bucketName)
                    .objects(objects)
                    .build());
            // 处理删除错误
            for (Result<DeleteError> error : errors) {
                System.err.println("Error deleting object: " + error.get().objectName() + ", " + error.get().message());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<String> listObjects(String bucketName, String prefix, boolean recursive, String start, Integer limit) {
        ListObjectsArgs.Builder argsBuilder = ListObjectsArgs.builder();
        argsBuilder.bucket(bucketName).recursive(recursive);
        if (prefix != null) {
            argsBuilder.prefix(prefix);
        }
        if (start != null) {
            argsBuilder.startAfter(start);
        }
        if (limit != null) {
            argsBuilder.maxKeys(limit);
        }

        Iterable<Result<Item>> results = client.listObjects(argsBuilder.build());
        return StreamSupport.stream(results.spliterator(), false)
                .map(result -> {
                    try {
                        return result.get();
                    } catch (MinioException e) {
                        throw new RuntimeException(e);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                })
                .map(Item::objectName)
                .collect(Collectors.toList());
    }

    @Override
    public InputStream getObject(String bucketName, String objectName) {
        try {
            return client.getObject(GetObjectArgs.builder().bucket(bucketName).object(objectName).build());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
