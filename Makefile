.ONESHELL:
compile :
	@javac -d ./out $(shell find ./Kandinsky -name '*.java')