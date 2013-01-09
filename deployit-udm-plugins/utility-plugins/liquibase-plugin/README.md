# Liquibase plugin #

This document describes the functionality provided by the Liquibase plugin.

See the **Deployit Reference Manual** for background information on Deployit and deployment concepts.

## Overview

The Liquibase plugin provides a simple way to use Liquibase as a drop in replacement of the official database plugin.
This is a simple integration where SQL rollbacks are not handled yet.

## Requirements

* **Deployit requirements**
	* **Deployit**: version 3.8+
	* **Other Deployit Plugins**: None

## Installation

You need to install Liquibase on a host accessible by the DeployIt server.

## Liquibase execution

At each deployment, liquibase "update" command is executed 

## Configuration

TBD