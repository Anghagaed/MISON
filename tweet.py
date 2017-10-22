#!/usr/bin/env python
# encoding: utf-8
# Source: https://nocodewebscraping.com/twitter-json-examples/

import tweepy #https://github.com/tweepy/tweepy
import json


#Twitter API credentials
consumer_key = "s95wwE7JG7DtLWbBHmgm8XP7p"
consumer_secret = "OEz4skWagLFdmuSVGGfxFJIfJ5z5BAhSCSglNqYXnHm9H82qEo"
access_key = "877999455940759552-kBjJ0rGPYVHdA3GXSsZXStp9QFSobzQ"
access_secret = "hcqHzFILYbcGaycdOw2dSDE9pDPbAFrqKExTqSUlaiydA"


def get_all_tweets(screen_name):
    
    #Twitter only allows access to a users most recent 3240 tweets with this method
    
    #authorize twitter, initialize tweepy
    auth = tweepy.OAuthHandler(consumer_key, consumer_secret)
    auth.set_access_token(access_key, access_secret)
    api = tweepy.API(auth)
    
    #initialize a list to hold all the tweepy Tweets
    alltweets = []    
    
    #make initial request for most recent tweets (200 is the maximum allowed count)
    new_tweets = api.user_timeline(screen_name = screen_name,count=10)
    
    #save most recent tweets
    alltweets.extend(new_tweets)
    
    #save the id of the oldest tweet less one
    oldest = alltweets[-1].id - 1
    
    #hang told me to
    i = 0

    #keep grabbing tweets until there are no tweets left to grab
    while i < 1:
        i = i + 1
        #all subsiquent requests use the max_id param to prevent duplicates
        #new_tweets = api.user_timeline(screen_name = screen_name,count=2,max_id=oldest)
        
        #save most recent tweets
        #alltweets.extend(new_tweets)
        
        #update the id of the oldest tweet less one
        oldest = alltweets[-1].id - 1

        print "...%s tweets downloaded so far" % (len(alltweets))
       
    #write tweet objects to JSON
    file = open('tweet.json', 'wb') 
    print "Writing tweet objects to JSON please wait..."
    for status in alltweets:
		json.dump(status._json,file,sort_keys = True)
		file.write(",\n")
    
    #close the file
    print "Done"
    file.close()

if __name__ == '__main__':
    #pass in the username of the account you want to download
    get_all_tweets("@Google")

