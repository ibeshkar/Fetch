# Fetch

Fetch is a library for server request (get,post,...)

# Overview

This library is simple and allow users to send REST
request to remote server and get response.

Some feature of Fetch :

* Get/Post request
* Cache response
* save cookies
* Simple error handling
* ...

# Usage

Step 1. Add it in your root build.gradle at the end of repositories:

```{
    allprojects {
        repositories {
            ...
            maven { url "https://jitpack.io" }
        }
   }
```

Step 2. Add the dependency

```{
    dependencies {
	        implementation 'com.github.ibeshkar:Fetch:1.4.0'
    }
```

Step 3. Use it in your project for remote request :

```{
    Fetch request = new RequestBuilder(this)
                    .setUrl("http://...")
                    .setMethod(Method.POST)
                    .setParam("p1", "...")
                    .setParam("p2", "...")
                    .setHeader("Auth", "...")
                    .setCaches()
                    .setCookies()
                    .create();

            request.start(new IResponse() {
                @Override
                public void OnSuccess(String response) {

                }

                @Override
                public void OnFailed(String error) {

                }
            });
```
