
https://kreuzberg.dev/getting-started/

Kreuzberg is a powerful pdf extraction api service
that we can use to extract data from pdf.

First you have to get it from git and the reference the path in the Docker-compose.yaml

cd ~/workspace
git clone https://github.com/Goldziher/kreuzberg.git

than change the Docker-compose.yaml the path to that repo

```yaml
version: "3.9"

services:
    kreuzberg:
        build:
            context: ~/workspace/kreuzberg   # repo root
            dockerfile: .docker/Dockerfile                             # use their file
        
```

Then build and run the image
```bash
docker-compose build --no-cache
docker-compose up -d        
```

We have a bruno rest collection where you can test if it is working.