#!/usr/bin/env bash

set -e

target="localhost"
port=3306
username="root"
# Note: password is "root".

mysql -h $target -P $port -u $username -p < init.sql
