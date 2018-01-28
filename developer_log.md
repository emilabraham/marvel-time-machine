# Developer Log

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
