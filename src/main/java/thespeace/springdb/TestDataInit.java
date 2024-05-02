package thespeace.springdb;

import thespeace.springdb.domain.Item;
import thespeace.springdb.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

@Slf4j
@RequiredArgsConstructor
public class TestDataInit {

    private final ItemRepository itemRepository;

    /**
     * <h2>확인용 초기 데이터 추가</h2>
     * <h3>{@code @EventListener(ApplicationReadyEvent.class)} : 스프링 컨테이너가 완전히 초기화를 다 끝내고,
     * 실행 준비가 되었을 때 발생하는 이벤트이다. 스프링이 이 시점에 해당 애노테이션이 붙은 initData() 메서드를 호출해준다.</h3>
     * <ul>
     *     <li>참고로 이 기능 대신 @PostConstruct 를 사용할 경우 AOP 같은 부분이 아직 다 처리되지 않은 시점에
     *         호출될 수 있기 때문에, 간혹 문제가 발생할 수 있다. 예를 들어서 @Transactional 과 관련된 AOP가
     *         적용되지 않은 상태로 호출될 수 있다.</li>
     *     <li>@EventListener(ApplicationReadyEvent.class) 는 AOP를 포함한 스프링 컨테이너가 완전히
     *         초기화 된 이후에 호출되기 때문에 이런 문제가 발생하지 않는다.</li>
     * </ul>
     */
    @EventListener(ApplicationReadyEvent.class) //스프링 컨테이너가 완전히 초기화를 다 끝내고, 실행 준비가 되었을 때 발생하는 이벤트.
    public void initData() {
        log.info("test data init");
        itemRepository.save(new Item("itemA", 10000, 10));
        itemRepository.save(new Item("itemB", 20000, 20));
    }

}
