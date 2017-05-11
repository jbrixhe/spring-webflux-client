package com.webfluxclient.handler;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import static java.util.Collections.singletonMap;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class DefaultReactiveInvocationHandlerTest {
    @Mock
    private ClientMethodHandler clientMethodHandler;
    
    @Test
    public void invoke() throws Throwable {
        Method methodOne = ReflectionUtils.findMethod(TargetOne.class, "methodOne");
        Object proxy = new Object();
        Object[] args = new Object[]{};
        InvocationHandler invocationHandler = buildInvocationHandler(methodOne);
        
        Object response = new Object();
        
        when(clientMethodHandler.invoke(same(args))).thenReturn(response);
    
        assertThat(invocationHandler.invoke(proxy, methodOne, args))
                .isSameAs(response);
    
        verify(clientMethodHandler).invoke(same(args));
        verifyNoMoreInteractions(clientMethodHandler);
    }
    
    @Test
    public void invoke_withWrongTargetMethod() throws Throwable {
        Method methodOne = ReflectionUtils.findMethod(TargetOne.class, "methodOne");
        Method methodTwo = ReflectionUtils.findMethod(TargetOne.class, "methodTwo");
        Object proxy = new Object();
        Object[] args = new Object[]{};
        InvocationHandler invocationHandler = buildInvocationHandler(methodOne);
        
        assertThatThrownBy(()-> invocationHandler.invoke(proxy, methodTwo, args))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Couldn't find a MethodHandler for the method");
    
        verifyZeroInteractions(clientMethodHandler);
    }
    
    private DefaultReactiveInvocationHandler buildInvocationHandler(Method method) {
        return new DefaultReactiveInvocationHandler(singletonMap(method, clientMethodHandler));
    }
    
    private class TargetOne {
        public void methodOne() {
        }
        
        public void methodTwo() {
        }
    }
}