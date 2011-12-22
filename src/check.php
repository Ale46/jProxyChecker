<?php
if (!getenv('HTTP_X_FORWARDED_FOR')){
	echo "Anonymous";
	exit;
}
if ((!getenv('HTTP_X_FORWARDED_FOR')) && (!getenv('HTTP_VIA')) && (!getenv('HTTP_PROXY_CONNECTION')){
	echo "High Anonymous";
	exit;
}
echo "Unknown";
?>