.PHONY: build test lint clean install

build:
	docker compose up --build builder

test:
	docker compose --profile test run --rm --build test

lint:
	docker compose run --rm --build builder gradle lintDebug --no-daemon

clean:
	docker compose down -v
	rm -rf output

install:
	adb install -r output/app-debug.apk
