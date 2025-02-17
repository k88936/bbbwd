.DEFAULT_GOAL := all
assets/hinge/.dep: assets/hinge/hinge_l1.png assets/hinge/hinge_l2.png assets/hinge/hinge_u1.png assets/hinge/hinge_u2.png assets/hinge/hinge_l.json assets/hinge/hinge_u.json
	@echo assets/hinge/hinge_l1.png assets/hinge/hinge_l2.png assets/hinge/hinge_u1.png assets/hinge/hinge_u2.png assets/hinge/hinge_l.json assets/hinge/hinge_u.json > assets/hinge/.dep
	cd ../&& python tools/AssetsGen.py assets_raw/assets/hinge
hinge: assets/hinge/.dep
assets/saw/.dep: assets/saw/saw_l.png assets/saw/saw_u1.png assets/saw/saw_u2.png assets/saw/saw_l.json assets/saw/saw_u.json
	@echo assets/saw/saw_l.png assets/saw/saw_u1.png assets/saw/saw_u2.png assets/saw/saw_l.json assets/saw/saw_u.json > assets/saw/.dep
	cd ../&& python tools/AssetsGen.py assets_raw/assets/saw
saw: assets/saw/.dep
assets/test_block/.dep: assets/test_block/test_block.height.png assets/test_block/test_block.png assets/test_block/test_block.json
	@echo assets/test_block/test_block.height.png assets/test_block/test_block.png assets/test_block/test_block.json > assets/test_block/.dep
	cd ../&& python tools/AssetsGen.py assets_raw/assets/test_block
test_block: assets/test_block/.dep
all: hinge saw test_block
default: all
clean:
	 rm assets/*/.dep
