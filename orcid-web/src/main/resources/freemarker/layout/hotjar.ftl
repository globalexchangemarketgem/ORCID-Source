<#if hotjarTrackingId != ''>
	<!-- Hotjar -->
	<script>
	    (function(h,o,t,j,a,r){
	        h.hj=h.hj||function(){(h.hj.q=h.hj.q||[]).push(arguments)};
	        h._hjSettings={hjid:${hotjarTrackingId},hjsv:6};
	        a=o.getElementsByTagName('head')[0];
	        r=o.createElement('script');r.async=1;
	        r.src=t+h._hjSettings.hjid+j+h._hjSettings.hjsv;
	        a.appendChild(r);
	    })(window,document,'https://static.hotjar.com/c/hotjar-','.js?sv=');
	</script>
	<script>
		var userId = orcidVar.realOrcidId || null;
        window.hj('identify', userId, {
			user_id	: userId,
		});
	</script>
	<!-- End Hotjar -->
 </#if>
