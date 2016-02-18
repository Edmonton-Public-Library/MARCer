MARCer
======

Overview
--------
I hate the fact that MARC 21 doesn't have any tools that do what I want to do. Marcedit is good but basically
a text editor and it has several problems. Perl has MARC::Batch and there is a little documentation to get 
you going, but nothing for when the rubber hits the road. I need something that I can just specify what the 
Cataloguers want and let'er go to it. 

MARCer is that tool.

The project started with a request from the LAT cataloguer Allison, who needed special handling of EBSCO A to Z 
records. 'Sometimes we need to add tags, sometimes remove tags. Then we always have to adjust the URL to electonic
resources. Oh, by the way, there are 58,000 of them.' Sigh. 

How can MARCer help
-------------------
MARCer reads a set of instructions from a command file. These instructions are nothing too impressive, mind you,
but they get job done, and they are extensible. Once the instructions are parsed and loaded the MARC 21 file is
read, then if conditions apply, editing commands are executed and a new MARC 21 file is produced complete with
changes.

The instructions are modelled on triples of the form <subject> <verb> <predicate>.

Command set
-----------

Comments (always welcome)
```
syntax: [#|REM|rem] <comments>
```
Example:
```
# Some comment or if you like...
REM (or rem) another comment.
```

Printing to screen
------------------

Print each record in the MARC file with the following.
```
record print
```

Delete tags
-----------
Sometimes you need to delete tags of a specific type. Use the delete command.
```
synatx: <tag> delete [matching <string>]
```
as in
```
035 delete
```
Sometimes you need to delete tags that contain a specific string. Leave out the keyword 'matching' and 
string predicate, and it will delete all the 035 tags in all the records.
```
035 delete matching EBZ
```

Writing MARC 21
---------------

You can output your changes to text or binary MARC with the following.
```
syntax: record[s] write <file.name> as [text|binary]
```
Example:
```
records write test.txt as text
```
outputs all the records in a single text file, while
```
record write test.mrc as binary
```
writes a given record as a binary MARC 21 file.

REM records write test.txt as text
REM record print
REM 003 print
REM leader print
REM 003 append http://some.nonsense.com/index.php and other stuff.
REM record print
REM 003 pre-pend afore string of some sort.
REM record print
REM 035 add test record content [eyes only]
REM record print
REM 151104nuuuuuuuuxx |||| s|||||||||||eng|d will succeed if used as below.
REM 008 if 23 == s then 008 print 
REM 008 print
REM 008   if 23 == s then 035 print
REM 035 print

REM ###### Untested.
REM set var debug = true
REM set var ignore_white_space = false ############# Don't use.
035 if 5 == S then 035 set 5 = p
035 print
records write test.mrc as binary
REM language filter eng then [print|write file.name as [text|binary]] 

REM language eng then records write bin_test.mrc as binary