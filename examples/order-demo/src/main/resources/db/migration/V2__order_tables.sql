create table if not exists orders (
  id uuid primary key,
  version int not null default 0,
  state text not null,
  created_at timestamptz not null,
  updated_at timestamptz not null
);
