# stats-challenge

Some backend challenge.

To aggregate events in one minute, used an array of size 60000 `number of millis in a minute` then append new events to it
in case of collision, the most recent is persisted.
To aggregate just combine contents of the array that fall into the last minute range.
