---
layout: page
title: "Example: Map Reduce"
---

``` java
@Capsule
class SupervisorCore {
    static final M = 10;
    static final R = 5;

    @Local Mapper[] mappers;
    @Local Reducer[] reducers;

    void run() {
        Future<Void>[] mapFutures = new Future<>[M];
        Result[] results = new Result[R];

        for (int i = 0; i < M; i++) {
            futures[i] = mappers.getItems(i);
        }
        for (Future<Void> f : mapFutures) {
            f.get();  // Wait until each mapper is done.
        }
        for (int i = 0; idx < R; idx++) {
            results[i] = reduce();
        }
        for (Result r : results) {
            System.out.println(r.toString());
        }
    }
}
```

``` java
@Capsule
class MapperCore {
    @Imported Reducer reducers;
    @Future Void map(Items w) {
        for (Item i : w) {

        }
        return null;
    }
}
```

``` java
@Capsule
class ReducerCore {
    private State s;
    Result reduce() {
        ...
        return result;
    }
}
```
