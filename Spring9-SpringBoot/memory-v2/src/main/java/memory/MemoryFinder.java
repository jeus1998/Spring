package memory;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MemoryFinder {
    /**
     * JVM에서 메모리 정보를 실시간으로 조회하는 기능
     * max: JVM이 사용할 수 있는 최대메모리, 이 수치가 넘으면 OOM 발생
     * total: JVM이 확보한 메모리(JVM은 처음부터 max까지 다 확보하지 않고 필요할 때 마다 조금씩 확보)
     * free: total 중에 사용하지 않는 메모리(JVM이 확보한 전체 메모리 중에 사용하지 않는 것)
     * used: JVM이 사용중인 메모리
     */
    public Memory get(){
        long max = Runtime.getRuntime().maxMemory();
        long total = Runtime.getRuntime().totalMemory();
        long free = Runtime.getRuntime().freeMemory();
        long used = total - free;
        return new Memory(used, max);
    }
    @PostConstruct
    public void init(){
        log.info("init memoryFinder");
    }

}
