import http from 'k6/http';
import { check, group, sleep } from 'k6';
import { Counter, Histogram, Rate } from 'k6/metrics';

const BASE_URL = __ENV.BASE_URL || 'http://localhost:8080';

// Custom metrics
const errorCount = new Counter('errors');
const successCount = new Counter('successes');
const responseTime = new Histogram('response_time');
const bookErrorRate = new Rate('book_errors');

export const options = {
  vus: 10,
  duration: '30s',
  thresholds: {
    'http_req_duration': ['p(95)<500', 'p(99)<1000'],
    'http_req_failed': ['rate<0.1'],
    'book_errors': ['rate<0.1'],
    'http_reqs': ['count>100']
  },
  stages: [
    { duration: '10s', target: 5 },
    { duration: '10s', target: 10 },
    { duration: '10s', target: 5 }
  ]
};

export function setup() {
  // Setup code - runs before tests
  console.log(`Starting tests against ${BASE_URL}`);
  return { baseUrl: BASE_URL };
}

export default function (data) {
  const baseUrl = data.baseUrl;

  group('Add Books', function () {
    const books = [
      { title: 'Clean Code', author: 'Robert C. Martin' },
      { title: 'Domain-Driven Design', author: 'Eric Evans' },
      { title: 'Building Microservices', author: 'Sam Newman' },
      { title: 'The Pragmatic Programmer', author: 'David Thomas' },
      { title: 'Design Patterns', author: 'Gang of Four' }
    ];

    for (let i = 0; i < books.length; i++) {
      const payload = JSON.stringify(books[i]);
      const params = {
        headers: { 'Content-Type': 'application/json' },
        tags: { name: 'AddBook' }
      };

      const res = http.post(`${baseUrl}/books`, payload, params);
      const success = check(res, {
        'post status is 201': (r) => r.status === 201,
        'response time < 500ms': (r) => r.timings.duration < 500
      });

      responseTime.add(res.timings.duration, { endpoint: '/books POST' });

      if (success) {
        successCount.add(1);
      } else {
        errorCount.add(1);
        bookErrorRate.add(1);
      }

      sleep(0.5);
    }
  });

  group('List Books', function () {
    const params = {
      headers: { 'Accept': 'application/json' },
      tags: { name: 'ListBooks' }
    };

    for (let i = 0; i < 5; i++) {
      const res = http.get(`${baseUrl}/books`, params);

      const success = check(res, {
        'get status is 200': (r) => r.status === 200,
        'response time < 300ms': (r) => r.timings.duration < 300,
        'has correct content type': (r) => r.headers['Content-Type'].includes('application/json'),
        'response body is array': (r) => Array.isArray(JSON.parse(r.body))
      });

      responseTime.add(res.timings.duration, { endpoint: '/books GET' });

      if (success) {
        successCount.add(1);
      } else {
        errorCount.add(1);
        bookErrorRate.add(1);
      }

      sleep(0.3);
    }
  });

  group('Mixed Load', function () {
    // 70% GET, 30% POST
    const random = Math.random();

    if (random < 0.7) {
      const res = http.get(`${baseUrl}/books`, {
        tags: { name: 'MixedGet' }
      });
      check(res, {
        'mixed get status is 200': (r) => r.status === 200
      });
    } else {
      const payload = JSON.stringify({
        title: `Book ${Math.random().toString(36).substring(7)}`,
        author: `Author ${Math.random().toString(36).substring(7)}`
      });

      const res = http.post(`${baseUrl}/books`, payload, {
        headers: { 'Content-Type': 'application/json' },
        tags: { name: 'MixedPost' }
      });

      check(res, {
        'mixed post status is 201': (r) => r.status === 201
      });
    }

    sleep(0.2);
  });
}

export function teardown(data) {
  console.log(`Tests completed against ${data.baseUrl}`);
  console.log(`Total errors: ${errorCount.value}`);
  console.log(`Total successes: ${successCount.value}`);
}
