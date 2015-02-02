# clj-payp

FIXME

## Prerequisites

You will need [Leiningen][1] 2.0 or above installed.
You will need MongoDB 2.6.7 or above.

To seed the databse go to /dump folder of application and run "mongorestore clj-payp/"

You will get one user with following data:
username: 999
password: password

Test credentials for paying with paypal in sandbox are:
Email: simke@happyfist.co
Password: 1234567890

[1]: https://github.com/technomancy/leiningen

## Running

To start a web server for the application, run:

    lein ring server

## License

Copyright © 2014 FIXME
