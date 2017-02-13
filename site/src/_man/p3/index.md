---
layout: man/part
part: 3
title: Part III. Implicit Parallelism
---

In coarse-grained concurrent applications, such as the simplified Asteroids game
illustrated in Chapter section 6, the main motivation is not necessarily to
achieve parallel execution but rather to correctly and safely model components
that are “logically autonomous”. These kinds of asynchronous, event-driven
systems are the obvious candidates for capsule-oriented design. However, the
capsule abstraction also adapts easily to other styles of parallel programming,
while retaining Panini’s advantages of abstracting away the concurrency control
mechanisms and ensuring data confinement.

- [Chapter 9. Master-Worker Pattern](/man/p3/ch9_master_worker_pattern.html)
- [Chapter 10. Leader-Follower Pattern](/man/p3/ch10_leader_follower_pattern.html)
- [Chapter 11. Pipeline Pattern](/man/p3/ch11_pipeline_pattern.html)
- [Chapter 12. Distributor Pattern](/man/p3/ch12_distributor_pattern.html)
