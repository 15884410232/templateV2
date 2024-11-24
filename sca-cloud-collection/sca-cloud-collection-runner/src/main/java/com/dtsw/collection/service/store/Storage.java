package com.dtsw.collection.service.store;

import org.apache.commons.io.IOUtils;
import org.springframework.util.unit.DataSize;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Collection;
import java.util.List;

/**
 * 文件服务类，定义存取文件的统一接口，
 *
 * @author Wangwang Tang
 * @since 2024-07-31
 */
public interface Storage {

    /**
     * 该文件系统的分隔符
     */
    String separator();

    /**
     * 判断指定桶是否存在
     *
     * @param bucketName 桶名称
     * @return 存在返回 true，否则返回 false
     */
    boolean existBucket(String bucketName);

    /**
     * 创建桶
     *
     * @param bucketName 桶名称
     */
    void makeBucket(String bucketName);

    /**
     * 创建目录对象
     *
     * @param bucketName 桶名称
     * @param objectName 对象名称
     */
    void putObject(String bucketName, String objectName);

    /**
     * 上传文件，实现注意项：
     * 1. 防止并发上传
     * 2. 上传完成前，对象不应该通过查询接口检测到
     *
     * @param bucketName 桶名称
     * @param objectName 对象名称
     * @param objectPath 文件本地路径
     */
    default void putObject(String bucketName, String objectName, Path objectPath) {
        try (InputStream inputStream = Files.newInputStream(objectPath, StandardOpenOption.READ)) {
            putObject(bucketName, objectName, inputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 上传对象，实现注意项：
     * 1. 防止并发上传
     * 2. 上传完成前，对象不应该通过查询接口检测到
     * 3. 该方法不会关闭对象输入流，请外部调用时注意关闭
     *
     * @param bucketName  桶名称
     * @param objectName  对象名称
     * @param inputStream 对象输入流
     */
    void putObject(String bucketName, String objectName, InputStream inputStream);

    /**
     * 合并分片文件，实现注意项：
     * 1. 防止重复合并
     * 2. 合并后需要删除分片对象
     * 3. 合并未完成时，合并后的对象不应该通过查询接口检测到
     *
     * @param bucketName        桶名称
     * @param partObjectNames   分片对象集合，需要按合并的顺序排列
     * @param composeObjectName 合并对象的名称
     */
    void composeObject(String bucketName, List<String> partObjectNames, String composeObjectName);

    /**
     * 获取对象数据大小
     *
     * @param bucketName 桶名称
     * @param objectName 对象名称
     * @return 数据大小
     */
    DataSize getObjectSize(String bucketName, String objectName);

    /**
     * 是否存在该对象
     *
     * @param bucketName 桶名称
     * @param objectName 对象名称
     * @return 存在返回 true，否则返回 false
     */
    boolean existObject(String bucketName, String objectName);

    /**
     * 删除对象，只删除文件或者空文件夹
     *
     * @param bucketName 桶名称
     * @param objectName 对象名称
     */
    default void removeObject(String bucketName, String objectName) {
        removeObject(bucketName, objectName, false);
    }

    /**
     * 删除对象
     *
     * @param bucketName 桶名称
     * @param objectName 对象名称
     * @param recursive  递归删除
     */
    void removeObject(String bucketName, String objectName, boolean recursive);

    /**
     * 批量删除对象，只删除文件或者空文件夹
     *
     * @param bucketName  桶名称
     * @param objectNames 对象名称集合
     */
    default void removeObjects(String bucketName, Collection<String> objectNames) {
        removeObjects(bucketName, objectNames, false);
    }

    /**
     * 批量删除对象
     *
     * @param bucketName  桶名称
     * @param objectNames 对象名称集合
     * @param recursive   递归删除
     */
    default void removeObjects(String bucketName, Collection<String> objectNames, boolean recursive) {
        if (objectNames != null && !objectNames.isEmpty()) {
            objectNames.forEach(objectName -> removeObject(bucketName, objectName, recursive));
        }
    }

    /**
     * 获取所有对象
     *
     * @param bucketName 桶名称
     * @return 符合条件的对象名称集合
     */
    default List<String> listObjects(String bucketName) {
        return listObjects(bucketName, null);
    }

    /**
     * 获取所有对象
     *
     * @param bucketName 桶名称
     * @param prefix     对象前缀
     * @return 符合条件的对象名称集合
     */
    default List<String> listObjects(String bucketName, String prefix) {
        return listObjects(bucketName, prefix, false);
    }

    /**
     * 获取所有对象
     *
     * @param bucketName 桶名称
     * @param prefix     对象前缀
     * @param recursive  递归
     * @return 符合条件的对象名称集合
     */
    default List<String> listObjects(String bucketName, String prefix, boolean recursive) {
        return listObjects(bucketName, prefix, recursive, null);
    }

    /**
     * 获取所有对象
     *
     * @param bucketName 桶名称
     * @param prefix     对象前缀
     * @param recursive  递归
     * @param start      起始对象
     * @return 符合条件的对象名称集合
     */
    default List<String> listObjects(String bucketName, String prefix, boolean recursive, String start) {
        return listObjects(bucketName, prefix, recursive, start, null);
    }

    /**
     * 获取所有对象
     *
     * @param bucketName 桶名称
     * @param recursive  递归
     * @return 符合条件的对象名称集合
     */
    default List<String> listObjects(String bucketName, boolean recursive) {
        return listObjects(bucketName, null, recursive);
    }

    /**
     * 获取所有对象
     *
     * @param bucketName 桶名称
     * @param recursive  递归
     * @param start      起始对象
     * @return 符合条件的对象名称集合
     */
    default List<String> listObjects(String bucketName, boolean recursive, String start) {
        return listObjects(bucketName, null, recursive, start);
    }

    /**
     * 获取所有对象
     *
     * @param bucketName 桶名称
     * @param recursive  递归
     * @param start      起始对象
     * @param limit      最大数量
     * @return 符合条件的对象名称集合
     */
    default List<String> listObjects(String bucketName, boolean recursive, String start, Integer limit) {
        return listObjects(bucketName, null, recursive, start, limit);
    }

    /**
     * 获取所有对象
     *
     * @param bucketName 桶名称
     * @param prefix     对象前缀
     * @param recursive  递归
     * @param start      起始对象
     * @param limit      最大数量
     * @return 符合条件的对象名称集合
     */
    List<String> listObjects(String bucketName, String prefix, boolean recursive, String start, Integer limit);

    /**
     * 下载对象
     *
     * @param bucketName 桶名称
     * @param objectName 对象前缀
     * @param outputStream 接收对象的输入流（该输出流需要调用者关闭）
     */
    default void getObject(String bucketName, String objectName, OutputStream outputStream) {
        try (InputStream inputStream = getObject(bucketName, objectName)) {
            IOUtils.copyLarge(inputStream, outputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 下载对象
     *
     * @param bucketName 桶名称
     * @param objectName 对象前缀
     * @return 对象的输入流
     */
    InputStream getObject(String bucketName, String objectName);
}
