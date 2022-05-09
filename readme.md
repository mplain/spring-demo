This is a demo project showcasing a Spring Boot microservice written in Kotlin.

Originally I used this project to teach Spring Boot to the students of Kotlin Backend course at Tinkoff Fintech School.

It's pizza store in a park, with two windows: one sells coffee, the other sells pizza. When you buy a coffee, you have
to wait a bit, then you get your coffee and a receipt (synchronous endpoint). When you buy a pizza, you get a receipt
right away, with an order number. You need to wait for a while before your order is read (asynchronous endpoint).

The application is separated into three layers: Controller, Service, Client / Dao. Business logic is encapsulated in the
Service layer, while the Controller layer is only a technical entrypoint (other types of entrypoints would include Kafka
and MQ listeners, as well as Scheduled jobs). Client and Dao classes are written in such a way as to be easily mocked in
tests. Tests are written using Kotest and Mockk.

I favor functional programming style and immutability over the classic object-oriented approach.
In my applications, everything is either a pipeline (service, bean), a model (data structure), or a utility class.
I do not like "gearboxes", complex classes that both hold data and process it.
Thus, I use Spring JDBC over JPA / ORM, and I don't have dedicated domain / entity models, or presentation / dto models.
