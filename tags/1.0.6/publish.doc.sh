#!/bin/bash

export VERSION=$1
export DOCDIR=build/docs

export SVNURL=https://eproperties.googlecode.com/svn/docs/$VERSION

echo "Publishing to $SVNURL"
sleep 5

cd $DOCDIR
echo Changed to $PWD

echo Making remote dir.
svn mkdir $SVNURL -m "" 

echo Importing
svn import . $SVNURL -m ""

echo Checking out for propset

svn co $SVNURL foo

cd foo

echo calling propsets
for file in `find . -name "*.html"`; do svn propset svn:mime-type text/html $file; done
for file in `find . -name "*.css"`; do svn propset svn:mime-type text/css $file; done
for file in `find . -name "*.txt"`; do svn propset svn:mime-type text/plain $file; done
for file in `find . -name "*.gif"`; do svn propset svn:mime-type image/gif $file; done

echo commiting
svn commit -m ""

echo cleaning up
cd ..
rm -rf foo
cd -

echo "Docs version $VERSION published to SVN."
echo "at url $SVNURL"

