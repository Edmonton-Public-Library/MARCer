MARCer
======

Overview
--------
I hate the fact that MARC 21 doesn't have any tools that do what I want to do. Marcedit is good but basically
a text editor and it has several problems. Perl has MARC::Batch and there is a little documentation to get 
you going, but nothing for when the rubber hits the road. I need something that I can just specify what the 
Cataloguers want and let'er go to it. For me MARCer is that tool.

The project started with a request from the LAT cataloguer Allison, who needed special handling of EBSCO A to Z 
records. 'Sometimes we need to add tags, sometimes remove tags. Then we always have to adjust the URL for electonic
resources. Oh, by the way, there are 58,000 of them.' Sigh. 

How can MARCer help
-------------------
MARCer reads a set of instructions from a command file. These instructions are nothing too impressive, mind you,
but they get job done, and they are extensible. Once the instructions are parsed and loaded the MARC 21 file is
read, then if conditions apply, editing commands are executed and a new MARC 21 file is produced complete with
changes.

The instructions are modelled on triples of the form.
```
<subject> <verb> <predicate>
```

The following are recognized commands
=====================================

Commenting
----------

Comments (always welcome)
```
syntax: [#|REM|rem] <comments>
```
Example:
```
# Some comment or if you like...
REM (or rem) another comment.
```

Setting variables
-----------------
There are some built-in variables that can be set.
```
syntax: set var <name> = <value>
```
Example:
```
set var debug = true
```

The following variables are currently recognized.
```
debug - boolean (true or false)
marc_file - string, name of the input marc file with fully qualified path if not in the current working directory.
verbose - boolean (true or fasle)
```


Printing to screen
------------------

Print each record in the MARC file with the following.
```
record print
```
All the 003 tags can be output like so.
```
003 print
```
or 
```
leader print
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

Modify tags
===========

Append to a tag
---------------
The are just text, so a find and replace in Marcedit would do the job, but then you have to 
compile the file back into MARC. Instead try the following.
```
<tag> append <text>
```
Example:
```
003 append http://some.nonsense.com/index.php
```

Pre-pend to a tag
-----------------
Sometimes you need to pre-pend information to a tag.
```
syntax: <tag> pre-pend <text>
```
Example
```
003 pre-pend afore string of some sort.
```

Add new tag
-----------
The following will add a new tag to the MARC record.
```
syntax: <tag> add <tag content>
```
Like so
```
035 add (OCLC) o2391456
```
The first non-white space character until the end of line character (EOL) is considered content for the tag.

Testing tags
------------
The contents of tags can be tested modestly. For example the query 'is there an "s" in the 23 position of the 008 tag?'
can be asked and more importantly answered. Consider the following 008 tag:
```
151104nuuuuuuuuxx |||| s|||||||||||eng|d 
```
Then test with
```
008 if 23 == s then 008 print
```
will succeed.

Note that only single characters can be tested like this, as this is no requirement for this functionality beyond that 
at this time, however it would be a relatively simple matter to extend.

Positional testing and setting
------------------------------

Changing a value at a specific position can be done with the set keyword.
```
syntax: <tag> set <position 0-based> = <character>
```
Example:
```
035 set 5 = p
```

This can be combined with a test.
```
035 if 5 == S then 035 set 5 = p
```

TODO commands
-------------------
Language filter. 


Running MARCer
==============

This application is written in Java 8.1 and can be run on command line. Currently the MARCer recognizes the following switches

```
-d Switch on debugging information.
-f <file>.mrc MARC 21 file to be read and analysed.
-i <commands.file> The file of commands to run. The file may have any extension.
-v Switch on verbose mode.
```