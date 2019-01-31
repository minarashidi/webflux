## Spring WebFlux

Spring WebFlux is a completely non-blocking reactive framework and part of Spring 5, it is indeed different than what we have in Spring MVC.
Spring WebFlux works on Servlet Container and also on Netty through Reactor Netty Project.

![alt text](https://docs.google.com/drawings/u/0/d/s5n8udTW1s2Cpq8-aTesWWw/image?w=224&h=205&rev=194&ac=1&parent=1rY2_zECduxmy6Hw6z1ExogvszxOUJgS0qgjWSkLZA8A)

In my Spring boot application, I have a dependency on WebFlux as spring-boot-starter-webflux, and at server startup, it says that the application is ready with Netty.

``` NettyWebServer: Netty started on port(s): 8080```


In the same application, if we use dependency for the spring-boot-starter-web, then logs would be printed as shown below:

``` TomcatWebServer: Tomcat started on port(s): 8080 ```

### Why should we use Spring WebFlux?

From thread perspective: whenever WebServer gets a **request**, there is a new servlet thread assigned to it, and takes the request and does some **processing**
, next is a DB call; this DB call is an IO operation between the WebServer and the DB, and during this IO call, 
our thread is **waiting** and blocked at this point until DB driver fetches the data and gives the data back to this particular thread, once it gets the data back it can send the data back as a response.

If during this waiting period(IO operation), there is another request coming into the WebServer then a new servlet thread will be assigned to that request and the same flow will be continue.

During those few MS while waiting for DB operations to be completed, consider a lot of concurrent requests come, if we're using a server like tomecat, we have a limited thread pool size=200
that means if there are 200 threads concurrently come then will be 200 threads assigned to them, and all 200 threads will make DB call and wait for a while that call will be completed and the response will be returned back
 also it means we can only serve 200 requests at time, and other requests should be waited.
 
 
 #### But what we want:
 Instead of waiting for IO operation to complete(DB call), will ask the the DB driver, call me once you have data and that thread go back and serve another request from other users
 and once the DB driver fetches the data, it makes the data call back and the same thread or any other thread which is free can be assigned to send this data back to the user as a response.
 This callback is event base mechanism 
 
 #### This mechanism is called event loop 
 In this mechanism, we have limited set of threads serving the requests from the user, whenever there is a request, we do some basic processing whatever required, 
 then immediately delegate all the IO operations to the DB driver as an event, on the other side, DB driver will take that event, does the IO operation and once the data is ready 
 will fire the event that I am ready with data (the event which represent the response) and any of the threads of the thread pool can take the data and send it back to the user 
 the whole mechanism operates with an event loop
 
 **This mechanism allows us:**
 limit our thread pool size; only one thread per CPU core because none of the threads is going to the blocking state 
 so we don't have thread switching; e.x, if we have 4 core processor, we can have only 4 threads and each thread assign to a core running all the time and then no thread is blocking 
 
 #### Here WebFlux helps us to handle event loop mechanism so we have higher scalability, CPU efficiently
 
Currently, we have these reactive DB drivers:
* mongoDb - cassandra - redis
* For relational DB, spring team introduced  R2DBC; which not production ready yet - also oracle is developing ADBA which will be Asynchronous and not reactive(but there is significant demand on supporting reactive for them and let's see what happened)



Spring WebFlux internally uses Project Reactor and its publisher implementations – Flux and Mono.

The new framework supports two programming models:

  * Annotation-based reactive components
  * Functional routing and handling

The second model(using handler and router function) does't use reflection and has, besides has quicker start up time
In this POC, I used this approach. 


#### Running the Reactor Netty server

   * Build using maven
   * Run WebfluxApplication class


Sample curl commands

Here are some sample curl commands that access resources exposed by this sample:

``` 
curl -v 'http://localhost:8080/customers'
curl -v 'http://localhost:8080/customers/1'
curl -v 'http://localhost:8080/customers' -H 'Accept: application/json'
```
