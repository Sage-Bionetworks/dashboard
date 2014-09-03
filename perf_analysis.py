#!/usr/bin/python

import csv
from pandas import DataFrame

records = [{'name': row[1].strip() + ' - ' + row[2].strip(), 'latency': int(row[3])} for row in csv.reader(open('dashboard-latencies.csv'))]

df = DataFrame(records)

count = df.groupby(['name']).count()
count.to_csv('dashboard-latencies-n.csv')

sum = df.groupby(['name']).sum()
sum.to_csv('dashboard-latencies-sum.csv')

mean = df.groupby(['name']).mean()
mean.to_csv('dashboard-latencies-mean.csv')

median = df.groupby(['name']).median()
median.to_csv('dashboard-latencies-median.csv')

