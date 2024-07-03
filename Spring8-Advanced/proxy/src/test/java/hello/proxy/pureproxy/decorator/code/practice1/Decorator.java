package hello.proxy.pureproxy.decorator.code.practice1;

import hello.proxy.pureproxy.decorator.code.Component;

/**
 * Decorator 추상화 GOF 디자인 패턴 만들기
 */
public abstract class Decorator implements Component{
    private Component component;
    public Decorator(Component component) {
        this.component = component;
    }
    protected Component getComponent(){
        return component;
    }
    public abstract String operation();
}
