
$(document).ready(function() {
	$("#quote").keyup(function(event){
	    if(event.keyCode == 13){
	        $("#getquotebutton").click();
	    }
	});
	
	$("#dashboard-message-field").corner("round 3px");
	$("#buy-links div").corner("round 5px");
	
	jQuery('#quote').click(function() { selectAllText(jQuery(this)) });
	
	if( $("#topranks").length > 0 ){
		setInterval(toprank, 20000);
	}
	
	if($("#currenttransactions").length){
		setInterval(loadCurrentTransactions, 20000);
	}
	
	if($("#balance").length > 0 ){
		setInterval(loadBalance, 20000);
	}
	
	if($("#portfolio").length > 0){
		setInterval(loadPortfolio, 20000);
	}
	
});


function writeAndGetQuote(quote){
	$("#quote").val(quote);
	$("#quote-hidden").val(quote);
	getquote();
}

function getquote() {
	// see dashboard.jsp
	var quote = $('#quote').val();
	$("#quote-hidden").val(quote);
	$.post('/getquote', {
		quote : quote
	}, function(data) {
        if(data.result){
        	if(data.resultCode == 'user-notfound'){
        		
        	}
        	else{
        		$("#total").html(commasep(data.respObj.stock.total));
        		$("#total-hidden").val(data.respObj.stock.total);
        		
        		$("#quote-id").val(data.respObj.stock.id);
        		$("#user-stock-val").val(parseInt(data.respObj.stock.total));
        		
        		var sold = calculateSold(data.respObj.stock.total, data.respObj.stock.sold);
        		$("#sold").html(commasep(sold));
        		$("#sold-hidden").val(sold);
        		
        		$("#available").html(commasep(data.respObj.stock.total - sold));
        		$("#available-hidden").val(data.respObj.stock.total - sold);
        		$("#dashboard-stock-follower-status").html("<a href=\'/stock/"+data.respObj.stock.id+"\'>"+data.respObj.stock.name+"</a>\'s follower status");
        		$("#dashboard-picture").attr("src",data.respObj.stock.pictureUrl);
        		$("#see-details-link").attr("href","/stock/"+data.respObj.stock.name);
        		if(data.resultCode != 'min-follower-count'){
        			$("#buy-links-row").show();
        			$("#sell-links-row").show();
        			writeBuySellLinks();
	        		if(data.respObj.percentage == 0){
	        			$("#user-stock").html("You do not have any " + quote);
	        		}
	        		else{
	        			$("#user-stock").html("You have " + parseInt(data.respObj.stock.total * data.respObj.percentage) + " of " + quote);
	        		}
        		}
        		else{
        			$("#buy-links-row").hide();
        			$("#sell-links-row").hide();
        			$("#user-stock").html(data.respObj.stock.name + " has <b>" + data.respObj.stock.total + "</b> follower. <br>You cannot buy followers if total is less than <b>" + data.respObj.minFollowerCount +"</b>");
        		}
        		showQuotePanel("userfound");
        	}
        }
        else{
        	
        }
	});
}

function calculateSold(total, soldPercentage){
	return parseInt(total * soldPercentage);
}

function writeBuySellLinks(){
	var cash = parseInt($("#cash-hidden").val());
	var available = parseInt($("#available-hidden").val());
	var quote = $("#quote-id").val();
	var min = cash;
	if(cash > available){
		min = available;
	}	
	
	var buyValues = new Array();
	var sellValues = new Array();
	if(min > 0){
		buyValues.push(min);
	}
	var i = min == 0 ? 0 : min.toString().length;
	$(".buy-sell-table").empty();
	for(; i > 0; i --){
		var amount = Math.pow(10,i - 1);
		buyValues.push(amount);
	}
	
	var sold = parseInt($("#sold-hidden").val());
	i = sold == 0 ? 0 : sold.toString().length;
	
	if(sold > 0 && sold != Math.pow(10,i - 1)){
		sellValues.push(sold);
	}	
	
	
	for(; i > 0; i --){
		var amount = Math.pow(10,i - 1);
		sellValues.push(amount);
	}
	
	i = 0
	while(true){
		var tr = $('<tr></tr>');
		var buyTd = $('<td></td>');
		var sellTd = $('<td></td>');
		if(i < buyValues.length){
			var div = $('<div></div>');
			$(div).html('Buy <br>' + commasep(buyValues[i]));
			$(div).attr('class', 'field-green');
			$(div).attr('onclick','buy('+quote+', '+buyValues[i]+');');
			$(div).corner("round 5px");
			$(buyTd).append($(div));
			
		}
		
		if(i < sellValues.length){
			var div = $('<div></div>');
			$(div).html( 'Sell <br>' + commasep(sellValues[i]));
			$(div).attr('class', 'field-red');
			$(div).attr('onclick','sell('+quote+', '+sellValues[i]+');');
			$(div).corner("round 5px");
			$(sellTd).append($(div));
		}
		
		tr.append(buyTd);
		tr.append(sellTd);
		$(".buy-sell-table").append(tr);
		
		i++;
		if(i > sellValues.length && i > buyValues.length){
			break;
		}
	}
}

function loadPortfolio(){
	$.post('/portfolio', {

	}, function(data){
		var stockInPortfolioList = data.stockInPortfolioList;
		$("#portfolio-table").empty();
		for(var i = 0; i < stockInPortfolioList.length;){
			var tr = $('<tr></tr>');
			for(var j = 0; j < 4; j++){
				var stockInPortfolio = null;
				
				if(i < stockInPortfolioList.length){
					stockInPortfolio = stockInPortfolioList[i];
				}
				
				var td = $('<td></td>');
				var table = $('<table></table');
				var tableTr = $('<tr></tr>');
				
				var tableTd1 = $('<td></td>');
				if(stockInPortfolio != null){
					var img = $('<img />').attr('class','twuser').attr('src',stockInPortfolio.pictureUrl);
					$(tableTd1).append(img);
				}
				
				var tableTd2 = $('<td></td>');
				if(stockInPortfolio != null){
					var tdA = $('<a>'+stockInPortfolio.stockName+'</a>');
					tdA.attr('onclick', 'writeAndGetQuote(\''+stockInPortfolio.stockName+'\');').attr('href','#');
					tableTd2.append(tdA).append('<br>$' + stockInPortfolio.amount);
				}
				$(tableTr).append(tableTd1);
				$(tableTr).append(tableTd2);			
				$(table).append(tableTr);	
				$(td).append(table);
				$(tr).append(td);
				i++;
			}
			$("#portfolio-table").append(tr);
		}
	});
}

function loadCurrentTransactions(){
	$.post('/transaction', {

	}, function(data){
		if(data != null){
			$("#current-transactions-table").empty();
			for(var i = 0; i < data.length; i++){
				var transactionRecord = data[i];
				var tr = $("<tr></tr>");
				if(i%2==0){
					$(tr).attr('class','odd');
				}
				var td = $('<td></td>');
				
				if(transactionRecord.transactionAction == 1){
					$(td).html("<a href=\"/user/"+transactionRecord.userName+"\">"+transactionRecord.userName+"</a> <span class=\"green\">bought</span> "+transactionRecord.amount+" <a onclick=\"writeAndGetQuote('"+transactionRecord.stockName+"');\" href=\"#\">"+transactionRecord.stockName+"</a>");
				}
				else{
					$(td).html("<a href=\"/user/"+transactionRecord.userName+"\">"+transactionRecord.userName+"</a> <span class=\"red\">sold</span> "+transactionRecord.amount+" <a onclick=\"writeAndGetQuote('"+transactionRecord.stockName+"');\" href=\"#\">"+transactionRecord.stockName+"</a>");
				}
				$(tr).append(td);
				$("#current-transactions-table").append(tr);
			}
		}
	});
}

function loadUserTransactions(){
	$.post('/transaction', {
		type: 'user'
	}, function(data){
		if(data != null){
			$("#your-transactions-table").empty();
			for(var i = 0; i < data.length; i++){
				var transactionRecord = data[i];
				var tr = $("<tr></tr>");
				if(i%2==0){
					$(tr).attr('class','odd');
				}
				var td = $('<td></td>');
				
				if(transactionRecord.transactionAction == 1){
					$(td).html("You <span class=\"green\">bought</span> "+transactionRecord.amount+" <a onclick=\"writeAndGetQuote('"+transactionRecord.stockName+"');\" href=\"#\">"+transactionRecord.stockName+"</a>");
				}
				else{
					$(td).html("You <span class=\"red\">sold</span> "+transactionRecord.amount+" <a onclick=\"writeAndGetQuote('"+transactionRecord.stockName+"');\" href=\"#\">"+transactionRecord.stockName+"</a>");
				}
				$(tr).append(td);
				$("#your-transactions-table").append(tr);
			}
		}
	});
}

function showQuotePanel(panel){
	var panels = new Array("userfound", "searchresult", "searchnoresult", "searchfailed");
	for ( var i=0, len=panels.length; i<len; ++i ){
		if(panels[i] == panel){
			$("#"+panels[i]).show();
		}
		else{
			$("#"+panels[i]).hide();
		}
	}	
}

function buy(stock, amount){
	$.post('/a/buy', {
		stock : stock,
		amount : amount
	}, function(data){
		$("#total").html(commasep(data.stockTotal));
		$("#total-hidden").val(data.stockTotal);
		
		var sold =  parseInt( data.stockTotal * data.stockSold);
		$("#sold").html( commasep(sold));
		$("#sold-hidden").val(sold);
		
		$("#available").html(commasep(data.stockTotal - sold));
		$("#available-hidden").val(data.stockTotal - sold);
		
		$("#cash_value").html(commasep(data.userCash) + "$");
		$("#cash-hidden").val(data.userCash);
		$("#portfolio_value").html(commasep(data.userPortfolio) + "$");
		$("#total_value").html(commasep(data.userCash + data.userPortfolio) + "$");
		writeBuySellLinks();
		$("#user-stock").html("You have <b>" + commasep(parseInt(data.userStock)) + "</b> of " + data.stockName);
		loadPortfolio();
		loadUserTransactions();
	});
}

function sell(stock, amount){
	$.post('/a/sell', {
		stock : stock,
		amount : amount
	}, function(data){
		$("#total").html(commasep(data.stockTotal));
		$("#total-hidden").val(data.stockTotal);
		
		var sold =  parseInt( data.stockTotal * data.stockSold);
		$("#sold").html( commasep(sold) );
		$("#sold-hidden").val(sold);
		
		$("#available").html(commasep(data.stockTotal - sold));
		$("#available-hidden").val(data.stockTotal - sold);
		
		
		$("#cash_value").html(commasep(data.userCash) + "$");
		$("#cash-hidden").val(data.userCash);
		$("#portfolio_value").html(commasep(data.userPortfolio) + "$");
		$("#total_value").html(commasep(data.userCash + data.userPortfolio) + "$");
		writeBuySellLinks();
		$("#user-stock").html("You have <b>" + commasep(parseInt(data.userStock)) + "</b> of " + data.stockName);
		loadPortfolio();
		loadUserTransactions();
	});
}

function setup(){
	var dbHost = $("#dbHost").val();
	var dbPort = $("#dbPort").val();
	var dbAdmin =$("#dbAdmin").val();
	var dbAdminPassword = $("#dbAdminPassword").val();
	var dbName = $("#dbName").val();
	var admin = $("#admin").val();
	var adminPassword = $("#adminPassword").val();
	var consumerKey = $("#consumerKey").val();
	var consumerSecret = $("#consumerSecret").val();

	$.post('/setup', {
		dbHost : dbHost,
		dbPort : dbPort,
		dbAdmin : dbAdmin,
		dbAdminPassword : dbAdminPassword,
		dbName : dbName,
		admin : admin,
		adminPassword : adminPassword,
		consumerKey : consumerKey,
		consumerSecret : consumerSecret
	}, function(data) {
		if(data.result){
			window.location = '/';
		}
		else{
			$(".error").empty();
			$(".error").append(document.createElement("ul"));
			for(var i=0,len=data.reasons.length; value=data.reasons[i], i<len; i++) {
				var li = document.createElement("li");
				$(li).text(value);
				$(".error ul").append(li)
			}
		}
	});
}

function toprank(){
	$("topranktable").empty();
	$.getJSON('/toprank', function(data) {
		$("#topranktable").empty();
		  for(var i = 0, length = data.length; i < length; i ++){
			  var rank = data[i];
			  var tr = $("<tr></tr>");
			  if(i%2==0){
				  tr.attr('class','odd');
			  }
			  $(tr).append($("<td class=\'rank-number\'>" + rank.rank + ". </td>"));
			  $(tr).append($("<td><img class=\'twuser\' src=\'" +rank.pictureUrl+ "\'/></td>"));
			  $(tr).append($('<td><a href=\'/user/'+rank.id+'\'>'+rank.userName+'</a> <br>'+commasep(rank.cash + rank.portfolio)+'$</td>'));
			  if(data.direction == 1){
				  $(tr).append($("<td><img src=\"/images/up.png\" /></td>"));
			  }
			  else{
				  $(tr).append($("<td><img src=\"/images/down.png\" /></td>"));
			  }			  
			  $("#topranktable").append(tr);
		  }
		});
}

function loadBalance(){
	$.post('/balance', {

	}, function(data){
		if(data != null){
			$("#balance_rank").html(data.rank + ".");
			if(data.direction == 1){
				$("#balance_direction").html("<img src=\"/images/up_small.png\" />");
			}
			else{
				$("#balance_direction").html("<img src=\"/images/down_small.png\" />");
			}
			
			$("#cash_value").html("$"+commasep(data.cash));
			$("#portfolio_value").html("$"+commasep(data.portfolio));
			$("#total_value").html("$"+commasep(data.total));
		}
	});
}


function commasep(number){
	var numberStr = number + '';
	var newNumber = '';
	var remaining = numberStr.length % 3 == 0 ? 3 : numberStr.length % 3;
    newNumber = numberStr.substr(0,remaining);
	
	for(var i = remaining; i < numberStr.length; i = i + 3){
		newNumber = newNumber + "," + numberStr.substr(i,3);
		
	}
	return newNumber;
}

function selectAllText(textbox) {
    textbox.focus();
    textbox.select();
}

