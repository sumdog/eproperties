#!/bin/bash

export VERSION=$1

if [ "X${VERSION}" == "X" ]
then
	echo "Usage:  $0 [version]"
	exit 1
fi

export DOCDIR=build/docs

export SVNURL=https://eproperties.googlecode.com/svn/docs/$VERSION

echo "Publishing to $SVNURL"
sleep 5

cd $DOCDIR
echo Changed to $PWD

echo Making remote dir, you have 5s to stop
echo svn mkdir $SVNURL -m "" 
sleep 5
svn mkdir $SVNURL -m "" 

sleep 5
echo Importing files below, you have 5s to stop
ls
sleep 5
svn import . $SVNURL -m ""

echo Checking out for propset
rm -rf /tmp/epdocs

svn co $SVNURL /tmp/epdocs

cd /tmp/epdocs

echo calling propsets
for file in `find . -name "*.html"`; do svn propset svn:mime-type text/html $file; done
for file in `find . -name "*.css"`; do svn propset svn:mime-type text/css $file; done
for file in `find . -name "*.txt"`; do svn propset svn:mime-type text/plain $file; done
for file in `find . -name "*.gif"`; do svn propset svn:mime-type image/gif $file; done

echo commiting
svn commit -m ""

echo cleaning up
cd -
rm -rf /tmp/epdocs

echo "Docs version $VERSION published to SVN."
echo "at url $SVNURL"

