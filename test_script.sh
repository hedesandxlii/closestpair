#!/bin/sh


# OBSOBSOBSOBSOBS Detta e systemspecifikt.


for FILE in test_files/*

do
	echo $FILE
    java -cp 'out\production\closestpair' closestpair $FILE
done