package hello.core.singleton;

public class SingletonService {
    private static final SingletonService instance = new SingletonService();

    public static SingletonService getInstance(){
        return instance;
    }

    // 생성자를 private -> 객체 생성(new 키워드) 자체를 불가능하게 만든다.
    private SingletonService(){}

    public void logic(){
        System.out.println("싱글톤 객체 호출");
    }
}
