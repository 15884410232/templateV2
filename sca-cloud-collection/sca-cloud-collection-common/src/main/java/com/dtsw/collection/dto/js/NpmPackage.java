package com.dtsw.collection.dto.js;
import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
public class NpmPackage {

    private Map<String, Object> _attachments;
    private String _id;
    private String _rev;
    private String description;
    private DistTags distTags;
    private String license;
    private List<Maintainer> maintainers;
    private String name;
    private String readme;
    private Time time;
    private Map<String, Version> versions;
//    private Bugs bugs;
    private String homepage;
    private List<String> keywords;
    private Repository repository;
    private String _sourceRegistryName;

    @Data
    public static class DistTags {
        private String beta;
        private String latest;
        private String legacy;
        private String next;
    }

    @Data
    public static class Maintainer {
        private String name;
        private String email;
    }

    @Data
    public static class Time {
        private String created;
        private String modified;
        private String version2157;
        private String version2156;
    }

    @Data
    public static class Version {
        private String name;
        private String version;
        private String description;
        private String main;
        private String typings;
        private Scripts scripts;
        //这个fass，数据源返回的格式异常，部分是数组，部分是对象。
//        private List<Faas> faas;
        private Repository repository;
        private String homepage;
        private List<String> keywords;
        private String license;
        private Bugs bugs;
        private String unpkg;
        private String style;
        private List<Dependencies> dependencies;
//        private Dependencies peerDependencies;
//        private Dependencies devDependencies;
        private String gitHead;
        private String id;
        private String npmVersion;
        private String nodeVersion;
        private NpmUser npmUser;
        private Dist dist;
        private List<Maintainer> maintainers;
        private NpmOperationalInternal npmOperationalInternal;
        private boolean hasShrinkwrap;
        private long publishTime;
        private long cnpmPublishTime;
        private String cnpmcorePublishTime;

        @Data
        public static class Scripts {
            private String bootstrap;
            private String buildFile;
            private String buildTheme;
            private String buildUtils;
            private String buildUmd;
            private String clean;
            private String deployBuild;
            private String deployExtension;
            private String devExtension;
            private String dev;
            private String devPlay;
            private String dist;
            private String i18n;
            private String lint;
            private String pub;
            private String test;
            private String testWatch;
        }

        @Data
        public static class Faas {
            private String domain;
            private String publicPath;
            private List<String> build;
        }

        @Data
        public static class Bugs {
            private String url;
        }

        @Data
        public static class Dependencies {
            private String name;
            private String version;
        }

        @Data
        public static class PeerDependencies {
            private String vue;
        }

        @Data
        public static class DevDependencies {
            private String vue;
            private String vueLoader;
            private String vueRouter;
            private String vueTemplateCompiler;
            private String vueTemplateEs2015Compiler;
            private String webpack;
            private String webpackCli;
            private String webpackDevServer;
            private String webpackNodeExternals;
            // 其他 devDependencies 字段
        }

        @Data
        public static class Dist {
            private String integrity;
            private String shasum;
            private String tarball;
            private int fileCount;
            private int unpackedSize;
            private String npmSignature;
            private int size;
            private boolean noattachment;
        }

        @Data
        public static class NpmUser {
            private String name;
            private String email;
        }

        @Data
        public static class NpmOperationalInternal {
            private String host;
            private String tmp;
        }
    }

    @Data
    public static class Bugs {
        private String url;
    }

    @Data
    public static class Repository {
        private String type;
        private String url;
    }
}