#!/bin/bash -e

[ -e reconf ] || ./reconf
[ -e configure ] || ./configure
make install