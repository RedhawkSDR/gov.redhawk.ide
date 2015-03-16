#!/bin/bash -e

echo Installing from $PWD
[ -e reconf ] && ./reconf
[ -e configure ] && ./configure
make install
