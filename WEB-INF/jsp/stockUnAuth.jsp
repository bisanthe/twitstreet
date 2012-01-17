<%@ taglib uri='/WEB-INF/tlds/template.tld' prefix='template'%>
<template:insert template='stockUnAuthTemplate.jsp'>
  <template:put name='title' content='twitstreet.com' direct='true'/>
  <template:put name='header' content='header.jsp' />
  <template:put name='topbar' content='topbar.jsp' />
  <template:put name='stockdetails' content='stockDetails.jsp' />
  <template:put name='topranks' content='topranks.jsp' />
  <template:put name='latesttransactions' content='latesttransactions.jsp' />
  <template:put name='footer' content='footer.jsp' />
</template:insert>