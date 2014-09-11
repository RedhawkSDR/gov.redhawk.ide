#!/bin/bash -e

echo Install directory $PWD
[ -e reconf ] && ./reconf
[ -e configure ] && ./configure
make install