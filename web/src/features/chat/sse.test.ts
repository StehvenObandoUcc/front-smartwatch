import { describe, expect, it } from 'vitest';

import { parseSse } from './sse';

function streamOf(chunks: string[]) {
  const encoder = new TextEncoder();
  return new ReadableStream<Uint8Array>({
    start(controller) {
      for (const chunk of chunks) controller.enqueue(encoder.encode(chunk));
      controller.close();
    },
  });
}

async function collect(chunks: string[]) {
  const events = [];
  for await (const event of parseSse(streamOf(chunks))) events.push(event);
  return events;
}

describe('parseSse', () => {
  it('lee delta, done y error aunque los eventos lleguen partidos', async () => {
    const events = await collect([
      'event: delta\ndata: {"text": "Esta no',
      'che"}\n\nevent: delta\ndata: {"text": " te toca"}\n\n',
      'event: done\ndata: {"remainingMessages": 27}\n\nevent: error\ndata: {"code": "chat_unavailable"}\n\n',
    ]);
    expect(events.map((e) => e.event)).toEqual(['delta', 'delta', 'done', 'error']);
    expect(JSON.parse(events[0]?.data ?? '{}')).toEqual({ text: 'Esta noche' });
    expect(JSON.parse(events[2]?.data ?? '{}')).toEqual({ remainingMessages: 27 });
  });

  it('acepta saltos de línea CRLF y ignora bloques sin datos', async () => {
    const events = await collect([': ping\r\n\r\nevent: done\r\ndata: {}\r\n\r\n']);
    expect(events).toEqual([{ event: 'done', data: '{}' }]);
  });
});
