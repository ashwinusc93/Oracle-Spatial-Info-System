<?php
//header('Access-Control-Allow-Origin: *');  
$page = $_GET['page'];
if ($page == "stock")
{
	$stock = $_GET['stock'];
	error_reporting(E_ALL);
	ini_set('display_errors', 'on');
    if(isset($stock))
    {
        set_error_handler(
		    create_function(
		        '$severity, $message, $file, $line',
		        'throw new ErrorException($message, $severity, $severity, $file, $line);'
		    )
		);
        $url_quote = "http://dev.markitondemand.com/MODApis/Api/v2/Quote/json?symbol=".$stock;
        
     	try {
	    	$str = file_get_contents($url_quote);
	        $json_quote = json_decode($str);
	        
	        
	        //$json_array = call_bing_search($stock);

	        $array = array("Name" => $json_quote->Name, "Symbol" => $json_quote->Symbol, "LastPrice"=> $json_quote->LastPrice, "Change" => $json_quote->Change, "ChangePercent" => $json_quote->ChangePercent, "Timestamp" => $json_quote->Timestamp, "MarketCap" => $json_quote->MarketCap, "Volume" => $json_quote->Volume, "ChangeYTD" => $json_quote->ChangeYTD, "ChangePercentYTD" => $json_quote->ChangePercentYTD, "High" => $json_quote->High, "Low" => $json_quote->Low, "Open" => $json_quote->Open, "Status" => $json_quote->Status, "Message" => $json_quote->Message);
	        
	        echo json_encode($array);
		}
		catch (Exception $e) {
    		http_response_code(404);
		}

    } else {
    	http_response_code(404);
    }
}
else if ($page == 'news')
{
	$stock = $_GET['stock'];
    error_reporting(E_ALL);
    ini_set('display_errors', 'on');
    if(isset($stock))
    {
         // Replace this value with your account key
        $accountKey = 'kkMcaKOm0g4NvRX6+U+ODA6SdmrTFIftKtAAuopAItw';

        $ServiceRootURL =  "https://api.datamarket.azure.com/Bing/Search";

        $WebSearchURL = $ServiceRootURL . '/v1/News?$format=json&Query=';

        $context = stream_context_create(array(
            'http' => array(
                'request_fulluri' => true,
                'header'  => "Authorization: Basic " . base64_encode($accountKey . ":" . $accountKey)
            )
        ));

        $request = $WebSearchURL . "'".$stock."'";

        //echo($request);

        $response = file_get_contents($request, 0, $context);

        $jsonobj = json_decode($response);
        
        $array = array();
        foreach($jsonobj->d->results as $value){
             
             $array[] = array("URL" => $value->Url, "Title" => $value->Title, "Content" => $value->Description, "Publisher" => $value->Source, "Date" => $value->Date);
        }
        
        
        echo json_encode($array);
    }
}
else if ($page == "history")
{
	$parameters = $_GET['parameters'];
    error_reporting(E_ALL);
    ini_set('display_errors', 'on');

    function jsonp_decode($jsonp, $assoc = false) { // PHP 5.3 adds depth as third parameter to json_decode
        // echo "JSONP: ";
        // echo $jsonp;
        if($jsonp[0] !== '[' && $jsonp[0] !== '{') { // we have JSONP
           $jsonp = substr($jsonp, strlen('(function () { })('));
        }
        return json_decode(trim($jsonp,')'), $assoc);
    }

    if(isset($parameters)){
        $url = "http://dev.markitondemand.com/Api/v2/InteractiveChart/jsonp?parameters=".$parameters;
        echo json_encode(jsonp_decode(file_get_contents($url)));
    }
}
else if ($page == "autocomplete")
{
    error_reporting(E_ALL);
    ini_set('display_errors', 'on');

    function jsonp_decode($jsonp, $assoc = false) { // PHP 5.3 adds depth as third parameter to json_decode
        // echo "JSONP: ";
        // echo $jsonp;
        if($jsonp[0] !== '[' && $jsonp[0] !== '{') { // we have JSONP
           $jsonp = substr($jsonp, strlen('(function () { })('));
        }
        return json_decode(trim($jsonp,')'), $assoc);
    }

    $url = "http://dev.markitondemand.com/api/v2/Lookup/jsonp?input=".$_GET["input"];
    echo json_encode(jsonp_decode(file_get_contents($url)));
}
   
?>
