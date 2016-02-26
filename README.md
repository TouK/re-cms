# re-cms

A [re-frame](https://github.com/Day8/re-frame) application designed to edit CMS content served inside your web application. 
There is example [Spring Boot]() starter backend [here](). 

## Development Mode

### Run application:

```
lein clean
lein figwheel dev
```

Figwheel will automatically push cljs changes to the browser.

Wait a bit, then browse to [http://localhost:3449](http://localhost:3449).

## Production Build

```
lein clean
lein cljsbuild once min
```
