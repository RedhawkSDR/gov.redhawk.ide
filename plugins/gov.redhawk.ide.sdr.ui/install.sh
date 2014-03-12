#!/bin/bash -e

[ -e configure ] || ./reconf
[ -e Makefile ] || ./configure
make install