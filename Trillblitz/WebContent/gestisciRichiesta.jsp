<%@ page language="java" contentType="text/html; charset=UTF-8" import="logica.*"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Gestisci Richiesta</title>
</head>
<body>
	<% String scelta = request.getParameter("scelta"); 
		int codice = Integer.parseInt(request.getParameter("codice"));		
		
		Richiesta richiesta = Richiesta.find(codice);
		
		if(scelta.equals("Si")) {
			richiesta.accetta();   %>
			
			La richiesta è stata accettata, ed è stato creto l'evento
			
	<%	}
		else {  
			richiesta.delete();    %>
			La richiesta è stata rifiutata e cancellata
	<% 	}    %>	
	
	
</body>
</html>