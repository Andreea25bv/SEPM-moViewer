INSERT INTO PERSON(name, tmdbid, biography, photopath) VALUES

  ('Michael Bay', 1337, 'biography', 'photoPath'),
  ('Will Smith',  1337,'biography', 'photoPath'),
  ('Martin Lawrence',  1337,'biography', 'photoPath'),

  ('Robert Zemeckis',  1337,'biography', 'photoPath'),
  ('Tom Hanks',  1337,'biography', 'photoPath'),

  ('Vin Diesel', 1337, 'biography', 'photoPath'),

  ('Peter Weir', 1337, 'biography', 'photoPath'),
  ('Harrison Ford', 1337, 'biography', 'photoPath'),
  ('Helen Mirren', 1337, 'biography', 'photoPath'),

  ('testPerson', 1337, 'testBiography', 'testPhotoPath');


INSERT INTO MOVIE(idTMDB,idIMDB, path, favorite,
                  title, runTime, year, posterPath, language, budget, revenue,
                  plot,
                  tagLine, similarFilms, genre, ignore) VALUES

  (8961, 0, 'C:\Programming\videomanager\qse-sepm-ws14-01\testing\Bad Boys 2.mpg', false,
   'Bad Boys II', 147, 2003, '/680X9apSqmAcebLg8evnnUeQNeI.jpg', 'en', '130000000', '262000000',
   'Martin Lawrence and Will Smith reunite as out-of-control trash-talking buddy cops Marcus Burnett and Mike Lowrey of the Miami Narcotics Task Force. Bullets fly, cars crash, and laughs explode as they pursue a whacked-out drug lord from the streets of Miami to the barrios of Cuba. But the real fireworks result when Lawrence discovers that playboy Smith is secretly romancing his sexy sister Syd.',
   'If you can''t stand the heat, get out of Miami.', '', 'Action,Adventure,Comedy,Crime,Thriller', false),

  (8358, 0, 'C:\Programming\videomanager\qse-sepm-ws14-01\testing\Cast Away.wmv', false,
   'Cast Away', 143, 2000, '/w515BrZvczKIxbHurG6HIiYYrba.jpg', 'en', '90000000', '429632142',
   'Chuck, a top international manager for FedEx, and Kelly, a Ph.D. student, are in love and heading towards marriage. Then Chuck''s plane to Malaysia ditches at sea during a terrible storm. He''s the only survivor, and he washes up on a tiny island with nothing but some flotsam and jetsam from the aircraft''s cargo. Can he survive in this tropical wasteland? Will he ever return to woman he loves?',
   'At the edge of the world, his journey begins.', '', 'Adventure,Drama', false),

  (2787, 0, 'C:\Programming\videomanager\qse-sepm-ws14-01\testing\The Chronicles Of Riddick.avi', false,
   'Pitch Black', 109, 2000, '/u6oDHb3z0uZNQHNdJwHJAW1jD4q.jpg', 'en', '23000000', '53187659',
   'After crash-landing on a seemingly lifeless planet, pilot Carolyn Fry and the remaining passengers -- including murderer Riddick and policeman William J. Johns -- encounter an army of creatures whose only weakness is light. As night approaches and the aliens emerge, the passengers must rely on Riddick''s powerful night vision to lead them through the darkness.',
   'Don''t be afraid of the dark. Be afraid of what''s in the dark', '', 'Action,Science Fiction,Thriller', false),

  (5175, 0, 'C:\Programming\videomanager\qse-sepm-ws14-01\testing\Rush Hour 2.mp4', false,
   'Rush Hour 2', 90, 2001, '/xG43wsvHpOp2QIj2JGQEvmK8dgC.jpg', 'en', '90000000', '347325802',
   'It''s vacation time for Carter as he finds himself alongside Lee in Hong Kong wishing for more excitement. While Carter wants to party and meet the ladies, Lee is out to track down a Triad gang lord who may be responsible for killing two men at the American Embassy. Things get complicated as the pair stumble onto a counterfeiting plot. The boys are soon up to their necks in fist fights and life-threatening situations. A trip back to the U.S. may provide the answers about the bombing, the counterfeiting, and the true allegiance of sexy customs agent Isabella.',
   'Get ready for a second Rush!', '', 'Action,Comedy,Crime,Thriller', false),

  (1272, 0, 'C:\Programming\videomanager\qse-sepm-ws14-01\testing\Sunshine.avi', false,
   'Sunshine', 107, 2007, '/fAqme02eg4PfWFUDqFQgyJKgLzp.jpg', 'en', '50000000', '32017803',
   '50 years from now the sun is dying and life on earth is threatened by arctic temperatures. Mankind puts together all its resources and sends a spaceship carrying a huge bomb designed to re-ignite the dying sun.',
   'If the sun dies, so do we.', '', 'Thriller', false),

  (2789, 0, 'C:\Programming\videomanager\qse-sepm-ws14-01\testing\The Chronicles Of Riddick.avi', false,
   'The Chronicles of Riddick', 119, 2004, '/tGNj5xxw5td0SBih1UmuqkHZ8ga.jpg', 'en', '105000000', '115772733',
   'Five years after the events in sci-fi film Pitch Black, escaped convict Riddick finds himself caught in the middle of a galactic war waged by Lord Marshal, the leader of a sect called the Necromongers. Riddick is charged with stopping the Necromonger army, all while rescuing an old friend from a prison planet and evading capture by bounty hunters.',
   'All the power in the universe can''t change destiny.', '', 'Action,Adventure,Science Fiction,Thriller', false),

  (11120, 0, 'C:\Programming\videomanager\qse-sepm-ws14-01\testing\The Mosquito Coast.avi', false,
   'The Mosquito Coast', 119, 1986, '/tXnfBOCTjfTYknXbTB6od8ncUP.jpg', 'en', '0', '14302779',
   'Allie Fox, an American inventor exhausted by the perceived danger and degradation of modern society, decides to escape with his wife and children to Belize. In the jungle, he tries with mad determination to create a utopian community with disastrous results.',
   'He went too far.', '', 'Action,Adventure,Drama', false);


INSERT INTO movie_has_person(mid, pid, role) VALUES

  (1, 1, 'Director'),
  (1, 2, 'Cast'),
  (1, 3, 'Cast'),
  (2, 4, 'Director'),
  (2, 5, 'Cast'),
  (3, 6, 'Cast'),
  (4, 10, 'TestRole'),
  (5, 10, 'TestRole'),
  (6, 6, 'Cast'),
  (7, 7, 'Director'),
  (7, 8, 'Cast'),
  (7, 9, 'Cast');


INSERT INTO RATING(mid, rating, source) VALUES

  (1, 4.1, 'RottenTomato'),
  (1, 4.5, 'UserRating'),
  (2, 7.4, 'RottenTomato'),
  (3, 5.6, 'RottenTomato'),
  (4, 5.4, 'RottenTomato'),
  (5, 6.8, 'RottenTomato'),
  (6, 4.6, 'RottenTomato'),
  (7, 6.4, 'RottenTomato');


INSERT INTO SUBTITLE(mid, language, commonWords, languagePath,
                     commonWordsCount, comment, numberOfWords) VALUES

  (1, 'en', 'commonWords', 'languagePath', 0, 'comment', 0),
  (1, 'de', 'commonWords', 'languagePath', 0, 'comment', 0),
  (2, 'es', 'commonWords', 'languagePath', 0, 'comment', 0),
  (3, 'en', 'commonWords', 'languagePath', 0, 'comment', 0),
  (4, 'en', 'commonWords', 'languagePath', 0, 'comment', 0),
  (5, 'de', 'commonWords', 'languagePath', 0, 'comment', 0),
  (6, 'fr', 'commonWords', 'languagePath', 0, 'comment', 0),
  (7, 'en', 'commonWords', 'languagePath', 0, 'comment', 0);
