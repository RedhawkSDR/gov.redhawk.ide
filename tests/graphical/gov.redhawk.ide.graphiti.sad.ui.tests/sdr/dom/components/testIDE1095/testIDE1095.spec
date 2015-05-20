# By default, the RPM will install to the standard REDHAWK SDR root location (/var/redhawk/sdr)
# You can override this at install time using --prefix /new/sdr/root when invoking rpm (preferred method, if you must)
%{!?_sdrroot: %define _sdrroot /var/redhawk/sdr}
%define _prefix %{_sdrroot}
Prefix:         %{_prefix}

# Point install paths to locations within our target SDR root
%define _sysconfdir    %{_prefix}/etc
%define _localstatedir %{_prefix}/var
%define _mandir        %{_prefix}/man
%define _infodir       %{_prefix}/info

Name:           testIDE1095
Version:        1.0.0
Release:        1%{?dist}
Summary:        Component %{name}

Group:          REDHAWK/Components
License:        None
Source0:        %{name}-%{version}.tar.gz
BuildRoot:      %{_tmppath}/%{name}-%{version}-%{release}-root-%(%{__id_u} -n)

BuildRequires:  redhawk-devel >= 2.0
Requires:       redhawk >= 2.0

# Interface requirements
BuildRequires:  bulkioInterfaces >= 2.0
Requires:       bulkioInterfaces >= 2.0

BuildArch: noarch


%description
Component %{name}


%prep
%setup -q


%build
# Implementation python
pushd python
./reconf
%define _bindir %{_prefix}/dom/components/testIDE1095/python
%configure
make %{?_smp_mflags}
popd


%install
rm -rf $RPM_BUILD_ROOT
# Implementation python
pushd python
%define _bindir %{_prefix}/dom/components/testIDE1095/python
make install DESTDIR=$RPM_BUILD_ROOT
popd


%clean
rm -rf $RPM_BUILD_ROOT


%files
%defattr(-,redhawk,redhawk,-)
%dir %{_prefix}/dom/components/testIDE1095
%{_prefix}/dom/components/testIDE1095/testIDE1095.scd.xml
%{_prefix}/dom/components/testIDE1095/testIDE1095.prf.xml
%{_prefix}/dom/components/testIDE1095/testIDE1095.spd.xml
%{_prefix}/dom/components/testIDE1095/python

