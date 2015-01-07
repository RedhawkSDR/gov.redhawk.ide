## Copyright (C) 2007   Sylvain Pelissier   <sylvain.pelissier@gmail.com>
##
## This program is free software; you can redistribute it and/or modify
## it under the terms of the GNU General Public License as published by
## the Free Software Foundation; either version 2 of the License, or
## (at your option) any later version.
##
## This program is distributed in the hope that it will be useful,
## but WITHOUT ANY WARRANTY; without even the implied warranty of
## MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
## GNU General Public License for more details.
##
## You should have received a copy of the GNU General Public License
## along with this program; If not, see <http://www.gnu.org/licenses/>.

## -*- texinfo -*-
## @deftypefn {Function File} fmmod (@var{x},@var{fc},@var{fs})
## Create the FM modulation of the signal x with carrier frequency fs. Where x is sample at frequency fs.
## @seealso{ammod,fmdemod,amdemod}
## @end deftypefn

function [s,t0,integral0] = fmmod(m,fc,sampleRate,freqdev,t0,integral0)

    if (nargin == 4)
	t0=0;
        integral0=0;
    elif (nargin == 5)
	integral=0;
    elif (nargin!=6)
	usage ('s = my_fmmod(m,fc,sampleRate,freqdev,t0,integral0)');
    endif

    l = length(m);
    t=[t0:1./sampleRate:(t0+(l-1)./sampleRate)];
    int_m = cumsum(m)+integral0;
    gain=2*pi.*freqdev/sampleRate;

    s = cos(2*pi.*fc.*t + gain.*int_m);
    t0=t(l)+1.0/sampleRate;
    integral0=int_m(l);
