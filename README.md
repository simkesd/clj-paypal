CLJ-PAYP
=======================================================


Prerequisites
=======================================================



-   Leiningen 2.0 or above
-   MongoDB 2.6.7 or above



Running the app
=========================================================



After you’ve installed leiningen and MongoDB you can prepare app for exexution. There are two steps you should do first:



1.  Run “lein deps” so that you get the appropriate dependencies
2.  Seed the database. On Linux you can do this by running “mongorestore dump/clj-payp/” from the root of application
3.  Start the server. On linux you can do this by running “lein ring server” from the root of application.



If everything was successful, after server was started your browser will load a tab on adress “localhost:3000”. Congratulations, you’ve started the app.


Testing data
======================================================



By seeding the database you’ve got some users and items, so you can test the application.



Application user
----------------------------------------------------------



admin user:

-   username: 999
-   password: password



Paypal user (sandbox account)
----------------------------------------------------------------------

-   email: simke@happyfist.co
-   password: 1234567890