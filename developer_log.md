# Developer Log

## 06.05.2018
Holy crap I'm stupid. I don't know why I didn't think of this sooner. I figured out how to get
around the 3000 call limit. Seriously, it's such an easy solution that I can't believe I didn't
think of it before.

I create my own backend that will call the Marvel API once per day and store the data. Effectively
it will run like a cron job. But it will only hit the Marvel API n times where n is the number of
years since the first published marvel comic. This works so much better.

Now the android app can truly be just a client side application. No need for a database. It will 
just call my own backend once or whenever it refreshes. My backend will have no rate limit. Which is
fine because I don't expect a ton of traffic. But it can definitely support more than 30 people lol.

Another reason why this is better is because I can store all the data on the backend as well. So I
can have historical data. So if they want to get all the comics that were published yesterday, then
it will be available. So I'll create another repo with the code for that.

And as if it couldn't get any better, I don't need to figure out how to hide the private API key now
in a client application. I just need to hide it in my single backend server which is far easier.

Unfortunately, that makes most of the code here moot. Whatever. I guess that's software.

Hopefully my current webserver can handle this. It might be better to try creating an S3 instance so
that I can get some more experience there.

## 27.1.2018
Alright. I have some choices for storage method, as shown 
[here](https://developer.android.com/training/data-storage/index.html). So here's the battle plan:

My rate limit is 3000 calls per day. That should be fine. I'm only going to make 1 call per year for
as long as Marvel was publishing comics. Let's overestimate that at 100. All the data from those
calls is going to be stored locally, so I won't ever have to make them again until the next day.
I guess unofficially that means 30 people can use my app. But I'm assuming they have some production
level API key I can get.

I need to store the results of those 100 calls somewhere locally. Then, on the next day, I can
remove the old stuff and make another 100 calls to fetch and store new data. It's all calls to the
/comics endpoint. That one endpoint had everything I need. Including the URL for the cover image.

I'm not sure how to deal with that cover image in Android. On the web, I'd just use the URL. But I
might have to download the image itself and put it in local storage. I'll deal with that when I come
to it.

Here is me going through all the options for storage:

Shared Preferences definitely isn't enough. It's meant for small settings stuff.

A file might work for the cover images. That all depends on if I need to download all the cover
images.

What the fuck is Room? Oh. It's just an abstraction over making direct SQLite calls. This seems
promising. At first, I thought a full database would be heavy handed, since it's effectively acting
like a cache. But whatever.

If they provide me a JPA like interface to interact with the DB, why use the DB directly?

Ding ding ding, I guess we have a winner. I'll stick with Room for now. This app is small enough
that I can change it easy enough.
