# spring-zeebe


[![Maven Central](https://maven-badges.herokuapp.com/maven-central/io.zeebe.spring/spring-zeebe/badge.svg)](https://maven-badges.herokuapp.com/maven-central/io.zeebe.spring/spring-zeebe)

[![Build Status](https://travis-ci.org/zeebe-io/spring-zeebe.svg?branch=master)](https://travis-ci.org/zeebe-io/spring-zeebe)
[![codecov](https://codecov.io/gh/zeebe-io/spring-zeebe/branch/master/graph/badge.svg)](https://codecov.io/gh/zeebe-io/spring-zeebe)
[![Project Stats](https://www.openhub.net/p/spring-zeebe/widgets/project_thin_badge.gif)](https://www.openhub.net/p/spring-zeebe)

bootify zeebe client and broker

Issue-Tracking: https://github.com/zeebe-io/spring-zeebe/issues

## Architecture

For Broker and Client two libraries are provided.
One holds the pure Spring setup and the other one is a starter that activates the behavior just by adding the
dependency. Using this setup, you can decide if you want to embedd the broker or client in a regular Spring application or if you want to use
the spring boot style "starter-magic" to activate.

The spring setup for both client and broker consists of the following major components:

* An objectFactory. This factory is responsible for creating the instance of broker or client.
* A SmartLifecycle: creates a new instance using the objectFactory and wraps it to a spring lifecycle, keeping the instance running as in server mode
* A configuration class providing instances of factory and lifecycle
* An `Enable` annotation that activates the configuration 

## Code of Conduct

This project adheres to the Contributor Covenant [Code of
Conduct](/.github/CODE_OF_CONDUCT.md). By participating, you are expected to uphold
this code. Please report unacceptable behavior to
code-of-conduct@zeebe.io.


