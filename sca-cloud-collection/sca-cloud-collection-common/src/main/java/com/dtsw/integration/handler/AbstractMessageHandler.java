package com.dtsw.integration.handler;

import com.dtsw.integration.endpoint.Concurrent;
import com.dtsw.integration.endpoint.MessageProcessor;
import com.dtsw.integration.endpoint.MessageRouter;
import com.dtsw.integration.endpoint.MessageSplitter;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.ResolvableType;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.integration.support.MutableMessageHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.PollableChannel;
import org.springframework.messaging.support.MessageBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 抽象消息处理器
 *
 * @author Wangwang Tang
 * @since 2024-11-01
 */
@Getter
public abstract class AbstractMessageHandler implements MessageHandler, ApplicationContextAware {

    public static final String PARENT_ID = "parentId";
    public static final String PARENT_IDS = "parentIds";
    private final ConversionService conversionService;
    @Setter(onMethod = @__({@Override}))
    protected ApplicationContext applicationContext;

    protected AbstractMessageHandler() {
        this(null);
    }

    protected AbstractMessageHandler(ConversionService conversionService) {
        this.conversionService = conversionService == null ? new DefaultConversionService() : conversionService;
    }

    protected void sendMessage(PollableChannel channel, Object result, MessageHeaders fromHeaders) {
        if (result instanceof Message<?> message) {
            MessageHeaders toHeaders = buildToMessageHeaders(message.getHeaders());
            channel.send(MessageBuilder.createMessage(message.getPayload(), toHeaders));
        } else {
            MessageHeaders toHeaders = buildToMessageHeaders(fromHeaders);
            channel.send(MessageBuilder.createMessage(result, toHeaders));
        }
    }

    private MessageHeaders buildToMessageHeaders(MessageHeaders headers) {
        MessageHeaders toHeaders = new MutableMessageHeaders(headers);
        UUID id = headers.getId();
        if (id != null) {
            toHeaders.put(PARENT_ID, id);
            // noinspection unchecked
            List<UUID> parentIds = (List<UUID>) headers.get(PARENT_IDS, List.class);
            if (parentIds != null) {
                List<UUID> mutableParentIds = new ArrayList<>(parentIds);
                mutableParentIds.add(id);
                toHeaders.put(PARENT_IDS, List.copyOf(mutableParentIds));
            } else {
                toHeaders.put(PARENT_IDS, List.of(id));
            }
        }
        return new MessageHeaders(toHeaders);
    }

    @SuppressWarnings("unchecked")
    protected <T> Message<T> convertMessage(Message<?> message, Class<?> thisType) {
        Class<T> resolveType = resolveRowType(thisType);
        if (resolveType == Void.class || resolveType == Object.class) {
            return (Message<T>) message;
        }
        Object payload = message.getPayload();
        if (resolveType.isAssignableFrom(payload.getClass())) {
            return MessageBuilder.createMessage((T) payload, message.getHeaders());
        }
        // 使用 Converter 进行转换，判断是否可转
        if (!conversionService.canConvert(payload.getClass(), resolveType)) {
            throw new IllegalArgumentException("Cannot convert " + payload.getClass() + " to " + resolveType);
        }
        T convertedPayload = conversionService.convert(payload, resolveType);
        if (convertedPayload == null) {
            throw new IllegalArgumentException("Convert result is null");
        }
        return MessageBuilder.createMessage(convertedPayload, message.getHeaders());
    }

    private <T> Class<T> resolveRowType(Class<?> thisType) {
        ResolvableType resolvableType = ResolvableType.forClass(thisType);
        List<Class<? extends Concurrent>> interfaces = List.of(MessageProcessor.class, MessageRouter.class, MessageSplitter.class);
        for (ResolvableType interfaceType : resolvableType.getInterfaces()) {
            Class<?> interfaceClass = interfaceType.resolve();
            if (interfaceClass == null || !interfaces.contains(interfaceClass)) {
                continue;
            }

            Class<?> resolve = resolvableType.as(interfaceClass).getGeneric(0).resolve();
            if (resolve != null) {
                // noinspection unchecked
                return (Class<T>) resolve;
            }
        }
        throw new RuntimeException("Can't resolve generic of type " + thisType);
    }

}
