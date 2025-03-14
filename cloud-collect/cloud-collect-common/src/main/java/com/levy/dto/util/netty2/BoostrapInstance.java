package com.levy.dto.util.netty2;

import io.netty.bootstrap.Bootstrap;

public class BoostrapInstance {

    Bootstrap bootstrap;

    static class BoostrapInstanceBuilder {
        private static final BoostrapInstance INSTANCE = new BoostrapInstance();
    }

}
