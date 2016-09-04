CHỨC NĂNG CỦA TOOL:
Có 2 chức năng tùy chọn:
1. Lấy tất cả các số điện thoại trong tất cả các comments trong fanpage	
2. Lấy các số điện thoại trong các comments của một bài post nào đó

CÁCH CHẠY TOOL:
*** Cách lấy page_name và post_id ***
	
	page_name: ví dụ với home page "https://www.facebook.com/xadontreotuongverygood/?fref=ts" thì page_name là "xadontreotuongverygood"

	post_id: ví dụ với post "https://www.facebook.com/xadontreotuongverygood/posts/964754263647531" thì post_id là "964754263647531"
	(để lấy được link của post, click vào thời gian của bài post đó)

Để chạy một trong 2 chức năng: click edit file "run.bat", sửa lại thành như sau:
1 (để lấy tất cả số điện thoại trong page "xadontreotuongverygood"):
java -Xmx1024m -cp facebook.jar httpget.crawler.PostsCrawler "xadontreotuongverygood" ""

2 (để lấy các số điện thoại trong một post, có post_id = "964754263647531", của page "xadontreotuongverygood"): 
java -Xmx1024m -cp facebook.jar httpget.crawler.PostsCrawler "xadontreotuongverygood" "964754263647531"
