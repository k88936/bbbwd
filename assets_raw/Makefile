.DEFAULT_GOAL := all
assets/hinge/.dep: assets/hinge/hinge_l1.png assets/hinge/hinge_l2.png assets/hinge/hinge_l3.png assets/hinge/hinge_u1.png assets/hinge/hinge_u2.png assets/hinge/hinge_l.height.png assets/hinge/hinge_l.json assets/hinge/hinge_u.json
	@echo assets/hinge/hinge_l1.png assets/hinge/hinge_l2.png assets/hinge/hinge_l3.png assets/hinge/hinge_u1.png assets/hinge/hinge_u2.png assets/hinge/hinge_l.height.png assets/hinge/hinge_l.json assets/hinge/hinge_u.json > assets/hinge/.dep
	cd ../&& python tools/AssetsGen.py assets_raw/assets/hinge
hinge: assets/hinge/.dep
assets/saw/.dep: assets/saw/saw_l.json assets/saw/saw_l.png assets/saw/saw_u.json assets/saw/saw_u1.png assets/saw/saw_u2.png
	@echo assets/saw/saw_l.json assets/saw/saw_l.png assets/saw/saw_u.json assets/saw/saw_u1.png assets/saw/saw_u2.png > assets/saw/.dep
	cd ../&& python tools/AssetsGen.py assets_raw/assets/saw
saw: assets/saw/.dep
all: hinge saw
default: all
clean:
	 rm assets/*/.dep
