<html>
<body>
<h1>tomcat 1</h1>
<h1>tomcat 1</h1>
<h1>tomcat 1</h1>
<h2>Hello World!</h2>

<!--springmvc上传文件-->
<form name="form1" action="/manage/product/upload.do" method="post" enctype="multipart/form-data">
    <input type="file" name="upload_file"/>
    <input type="submit" value="springmvc upload"/>
</form>

<!--springmvc上传文件-->
<form name="form2" action="/manage/product/richtext_img_upload.do" method="post" enctype="multipart/form-data">
    <input type="file" name="upload_file"/>
    <input type="submit" value="richtextt upload"/>
</form>
</body>
</html>
