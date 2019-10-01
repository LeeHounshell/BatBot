#!/usr/bin/python3

import socket
import os
import sys


try:
    image_name = sys.argv[1]
except IndexError:
    image_name = '/tmp/capture.jpg'


def client(message):
    host = socket.gethostbyname("localhost")    # get local machine name
    port = 9310    # Make sure it's within the > 1024 $$ <65535 range
    
    s = socket.socket()
    s.connect((host, port))
    
    s.send(message.encode('utf-8'))
    data = s.recv(1024).decode('utf-8')
    print('I see: ' + data)
    s.close()


if __name__ == '__main__':
    client(image_name)
