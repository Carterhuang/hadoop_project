#!/bin/bash

cd "raw_input"

if [ ! -e "nodes.txt" ];
then
    wget "http://edu-cornell-cs-cs5300s14-project2.s3.amazonaws.com/nodes.txt"
fi

if [ ! -e "edges.txt" ];
then
    wget "http://edu-cornell-cs-cs5300s14-project2.s3.amazonaws.com/edges.txt"
fi

if [ ! -e "blocks.txt" ];
then
    wget "http://edu-cornell-cs-cs5300s14-project2.s3.amazonaws.com/blocks.txt"
fi

cd ..

if [ ! -e "./input/input.txt" ]
then
    cd "raw_input"
    python processInput.py nodes.txt edges.txt blocks.txt input.txt
    cp input.txt ../input/input.txt
fi
