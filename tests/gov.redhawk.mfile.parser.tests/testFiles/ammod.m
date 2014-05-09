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
## @deftypefn {Function File} ammod (@var{x},@var{fc},@var{sampleRate})
## Create the AM modulation of the signal x with carrier frequency fc. Where x is sample at frequency sampleRate.
## @seealso{amdemod,fmmod,fmdemod}
## @end deftypefn


function [y,t0] = ammod(x,fc,sampleRate,t0)
    if (nargin == 3)
	t0=0
    elif (nargin!=4)
	usage ("ammod(x,sampleRate,fc,t0)");
    endif
    l = length(x);
    t=[t0:1./sampleRate:(t0+(l-1)./sampleRate)];

    y = x.*cos(2.*pi.*fc.*t);
    t0=t(l)+1.0/sampleRate;
